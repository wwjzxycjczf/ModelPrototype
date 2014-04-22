package org.act.util;
import com.wolfram.jlink.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class MathematicaTask extends Task implements PacketListener {

    static KernelLink ml = null;

    String exe = null;
    String cmdLine = null;

    boolean freshKernel = false;
    boolean quit = false;
    String runFile = null;
    int timeout = Integer.MAX_VALUE;
    String timeoutProperty = null;
    boolean failOnError = true;

    String code = "";
    String failMsg = null;


    /***********************  Attribute Setters  **********************/

    public void setExe(String exe) {
        this.exe = exe;
    }

    public void setCmdline(String cmdLine) {
        this.cmdLine = cmdLine;
    }

    public void setFresh(boolean fresh) {
        this.freshKernel = fresh;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }

    public void setRunfile(String runFile) {
        this.runFile = runFile;
    }

    public void setTimeout(int seconds) {
        this.timeout = seconds;
    }

    public void setTimeoutproperty(String timeoutProperty) {
        this.timeoutProperty = timeoutProperty;
    }

    public void setFailonerror(boolean failOnError) {
        this.failOnError = failOnError;
    }


    // Called by Ant with the text of the CDATA section, which is the code to execute.
    //
    public void addText(String code) {
        this.code = code;
    }


    // Called from Mathematica via the AntFail function to force a build failure.
    //
    public void setFail(String msg) {
        failMsg = msg;
    }


    /**************************  Execute  **************************/

    public void execute() throws BuildException {

        if (freshKernel)
            closeKernel();

        if (ml == null)
            ml = initKernel();

        TimeoutThread timeoutThread = null;

        try {
            timeoutThread = startTimeoutThread();

            ml.putFunction("EvaluatePacket", 1);
            ml.putFunction("Set", 2);
              ml.putSymbol("$this");
              ml.put(this);
            ml.discardAnswer();

            if (runFile != null) {
                ml.putFunction("EvaluatePacket", 1);
                ml.putFunction("Get", 1);
                ml.put(runFile);
                ml.discardAnswer();
            }
            if (code != null && !code.equals("")) {
                ml.evaluate(code);
                ml.discardAnswer();
            }
            // failMsg comes from a deliberate call to AntFail[]. Always throw.
            if (failMsg != null)
                throw new BuildException(failMsg, getLocation());
        } catch (MathLinkException e) {
            boolean wasTimeout = timeoutThread != null && timeoutThread.timeExpired;
            if (wasTimeout && timeoutProperty != null)
                getProject().setNewProperty(timeoutProperty, "true");
            String msg = wasTimeout ?
                            "Mathematica task killed on expired timeout of " + timeout + " seconds." :
                            "Mathematica task had a link error: " + ml.errorMessage();
            if (failOnError)
                throw new BuildException(msg, e, getLocation());
            else
                log(msg);
        } finally {
            killTimeoutThread(timeoutThread);
            failMsg = null;
            //setDefaults();
            if (quit || ml.error() != MathLink.MLEOK)
                closeKernel();
        }
    }


    protected KernelLink initKernel() {

        KernelLink ml = null;
        String[] args = new String[]{"-linkmode", "launch", "-linkname", ""};
        boolean useArray = false;

        if (exe != null) {
            useArray = true;
            String quoteChar = Utils.isWindows() ? "" : "'";
            args[3] = quoteChar + exe + quoteChar + " -mathlink";
        } else if (cmdLine == null) {
            throw new BuildException("Must specify exe or cmdline attribute to control kernel launch.", getLocation());
        }

        try {
            if (useArray) {
                // Simple kernel launch.
                ml = MathLinkFactory.createKernelLink(args);
                ml.discardAnswer();
            } else {
                ml = MathLinkFactory.createKernelLink(cmdLine);
                ml.connect();
            }
            ml.enableObjectReferences();
            ml.evaluateToInputForm(startupCode, 0);
            // Make the link a terminating link, so that if user kills the Ant task the kernel will die
            // as quickly as possible.
            ml.evaluateToInputForm("MathLink`SetTerminating[$ParentLink, True]", 0);
            ml.addPacketListener(this);
        } catch (MathLinkException e) {
            throw new BuildException("Failed to launch or connect to Mathematica kernel: " + e.getMessage(), e, getLocation());
        }
        return ml;
    }


    protected void closeKernel() {

        if (ml!= null) {
            ml.terminateKernel();
            ml.close();
            ml = null;
        }
    }

    protected TimeoutThread startTimeoutThread() {

        // Don't even start unless a timeout was set.
        if (timeout < Integer.MAX_VALUE) {
            long startTime = System.currentTimeMillis();
            TimeoutThread timeoutThread = new TimeoutThread(ml, startTime + timeout * 1000);
            timeoutThread.start();
            return timeoutThread;
        } else {
            return null;
        }
    }

    protected void killTimeoutThread(TimeoutThread timeoutThread) {

        if (timeoutThread != null) {
            timeoutThread.kill = true;
            try { timeoutThread.join(); } catch (InterruptedException e) {}
        }
    }


    // Empty Javadoc comment to avoid importing inherited one.
    /** &nbsp;
    */
    public boolean packetArrived(PacketArrivedEvent evt) throws MathLinkException {
         if (evt.getPktType() == MathLink.TEXTPKT) {
             KernelLink ml = (KernelLink) evt.getSource();
             log(ml.getString());
         }
         return true;
     }


    private static String startupCode =
        "Ant[obj_String] :=                                                     " +
        "   Switch[ToLowerCase[obj],                                            " +
        "       \"project\",                                                    " +
        "           Ant[\"target\"]@getProject[],                               " +
        "       \"target\",                                                     " +
        "           $this@getOwningTarget[],                                    " +
        "       \"task\",                                                       " +
        "           $this,                                                      " +
        "       \"location\",                                                   " +
        "           $this@getLocation[],                                        " +
        "       _,                                                              " +
        "           AntLog[\"Unknown object type in Ant function: \" <> obj];   " +
        "           $Failed                                                     " +
        "   ];                                                                  " +
        "AntTask[name_String, args___?OptionQ] :=                               " +
        "   JavaBlock[                                                          " +
        "      Module[{task, attrNames, attrVals},                              " +
        "          task = Ant[\"project\"]@createTask[name];                    " +
        "          attrNames =                                                  " +
        "             StringReplace[#, a_ ~~ b___  :> ToUpperCase[a] <> b]& /@  " +
        "                 First /@ Flatten[{args}];                             " +
        "          attrVals = Last /@ Flatten[{args}];                          " +
        "          With[{meth = ToExpression[\"set\" <> #1]},                   " +
        "              task@meth[#2]                                            " +
        "          ]& @@@ Thread[{attrNames, attrVals}];                        " +
        "          task@perform[]                                               " +
        "      ]                                                                " +
        "   ];                                                                  " +
        "AntLog[msg_String] := Ant[\"project\"]@log[msg];                       " +
        "AntLog[e_] := AntLog[ToString[e, FormatType->InputForm]];              " +
        "AntProperty[p_String] := Ant[\"project\"]@getProperty[p];              " +
        "AntSetProperty[p_String, val_String] := Ant[\"project\"]@setProperty[p, val];" +
        "AntReference[ref_String] := Ant[\"project\"]@getReference[ref];        " +
        "AntFail[msg_String] := ($this@setFail[msg]; Abort[])";


    // Implements timeout feature.
    private static class TimeoutThread extends Thread {

        private KernelLink ml;
        private long endTime;
        volatile boolean kill = false; // Whether to kill this thread (not the kernel).
        volatile boolean timeExpired = false; // Whether the timeout triggered.

        TimeoutThread(KernelLink ml, long endTime) {

            super("MathematicaTask Kernel-Killer Thread");
            setDaemon(true);
            this.ml = ml;
            this.endTime = endTime;
        }

        public void run() {

            while(!kill && System.currentTimeMillis() < endTime) {
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
            if (!kill) {
                timeExpired = true;
                ml.terminateKernel();
            }
        }
    }
}