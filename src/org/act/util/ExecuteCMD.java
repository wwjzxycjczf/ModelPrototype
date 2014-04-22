package org.act.util;

import com.wolfram.jlink.*;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;


import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Vector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

import com.wolfram.jlink.ui.InterruptDialog;
import com.wolfram.jlink.ui.SyntaxTokenizer;
import com.wolfram.jlink.ui.BracketMatcher;
public class ExecuteCMD {
	private String cmd = "";
	private KernelLink ml;
	private double lastTiming = 0.;
	private boolean feGraphics = false;
	private boolean fitGraphics = false;
	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	
	public void excute(){
		Runnable evalTask = new EvalTask();
		new Thread(evalTask).start();
	}
	
	public  void setLink(KernelLink ml) {
		this.ml = ml;
	}
	public KernelLink getLink() {
		return ml;
	}
	
	class EvalTask implements Runnable {

		public void run() {

			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
//						setEditable(false);
//						setInputMode(false);
//						setCursor(Cursor
//								.getPredefinedCursor(Cursor.WAIT_CURSOR));
					}
				});
			} catch (Exception e) {
			}

			long start = System.currentTimeMillis();

			try {
				// Have to do this differently if the link to Mathematica is
				// also the StdLink. This will be the case when
				// this pane is being used in a program that is being scripted
				// from Mathematica. This is a relatively unusual case.
				// We must call requestTransaction before each part of the
				// 3-part computation sequence. This means we
				// cannot wrap the entire sequence in a synchronized(ml) block.
				// The cost of that is that weird things could happen
				// if the user tried to use Mathematica for computations while a
				// computation from this pane was in progress,
				// because the settings made in preEval() could still be in
				// effect . It is not very likely that a user would try to do
				// this.
				if (ml.equals(StdLink.getLink())) {
					StdLink.requestTransaction();
					synchronized (ml) {
						preEval();
					}
					StdLink.requestTransaction();
					synchronized (ml) {
						ml.putFunction("EnterTextPacket", 1);
						ml.put(cmd);
						ml.discardAnswer();
						System.out.println("ml:"+ml.getString());
					}
					StdLink.requestTransaction();
					synchronized (ml) {
						postEval();
					}
				} else {
					// The more typical case. This pane is being used in a
					// standalone program.
					synchronized (ml) {
						preEval();
						ml.putFunction("EnterTextPacket", 1);
						ml.put(cmd);
						ml.discardAnswer();
						System.out.println("ml:"+ml.getString());
						postEval();
					}
				}
			} catch (MathLinkException e) {
				if (!ml.clearError() || e.getErrCode() == 11)
					// error 11 is "other side closed the link"
					closeLink();
				else
					ml.newPacket();
			}

			setLastTiming((System.currentTimeMillis() - start) / 1000.);

//			try {
//				SwingUtilities.invokeAndWait(new Runnable() {
//					public void run() {
//						setCaretPosition(getDoc().getLength());
//						getCaret().setVisible(true);
//						undoManager.discardAllEdits();
//						if (getLink() != null) {
//							setInputMode(true);
//							setEditable(true);
//							setCursor(Cursor
//									.getPredefinedCursor(Cursor.TEXT_CURSOR));
//						} else {
//							// Will get here if an unrecoverable link error
//							// caused closeLink() to be called above.
//							setCursor(Cursor
//									.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//						}
//					}
//				});
//			} catch (Exception e) {
//			}
		}

		// The idea of preEval and postEval is to allow this in/out window to
		// coexist with other
		// interfaces to the same kernel. Thus, we cannot make any persistent
		// changes to the state
		// of the kernel (other than those initiated by the user, of course). We
		// need to set up some
		// properties and then clear them out after each eval.

		private void preEval() throws MathLinkException {

			int paneWidthInChars = cmd.length();
			String df = "(LinkWrite[$ParentLink, DisplayPacket[EvaluateToImage[#, "
					+ (feGraphics ? "True" : "False")
					+ (fitGraphics ? ", ImageSize->{"
							+ (cmd.length() - 10)
							+ ", Automatic}"
							: "") + "]]]; #)&";
			ml.evaluate(// Use of the JLink`Private` context for these temp
						// variables is arbitrary.
			"{JLink`Private`cfv, JLink`Private`sopts, JLink`Private`ddf} = {FormatValues[Continuation], Options[\"stdout\"], $DisplayFunction} ; "
					+ "Format[Continuation[_], OutputForm] = \"\" ; "
					+ "SetOptions[\"stdout\", FormatType -> OutputForm, CharacterEncoding -> \"Unicode\", PageWidth -> "
					+ paneWidthInChars
					+ "] ; "
					+ "$DisplayFunction = "
					+ df
					+ ";");
			ml.discardAnswer();
		}

		private void postEval() throws MathLinkException {

			ml.evaluate("FormatValues[Continuation] = JLink`Private`cfv ; "
					+ "SetOptions[\"stdout\", JLink`Private`sopts] ; "
					+ "$DisplayFunction = JLink`Private`ddf ;");
			ml.discardAnswer();
		}

	}
	void closeLink() {
		if (ml != null) {
			ml.close();
			ml = null;
		}
	}

	public double getLastTiming() {
		return lastTiming;
	}

	public void setLastTiming(double lastTiming) {
		this.lastTiming = lastTiming;
	}
}
