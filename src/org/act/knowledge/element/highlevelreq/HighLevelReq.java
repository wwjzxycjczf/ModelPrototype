package org.act.knowledge.element.highlevelreq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class HighLevelReq {

	private int highlevelreqnum = 0;
	private String savePath = "resources/high-level-requirements";
	private File saveDir = new File(savePath);
	public synchronized void AddHighlevelreq(String highlevelreqstr) throws FileNotFoundException{
		if(!saveDir.isDirectory())
			saveDir.mkdir();
		String filename = highlevelreqnum+".xml";
		PrintStream pstream = new PrintStream(new FileOutputStream(saveDir+filename));  
		pstream.println(highlevelreqstr);// 往文件里写入字符串
		highlevelreqnum++;
	}
}
