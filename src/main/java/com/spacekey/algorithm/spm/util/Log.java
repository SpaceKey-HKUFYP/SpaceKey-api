package com.spacekey.algorithm.spm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spacekey.algorithm.global.Config;

/**
 * @author fangyixiang
 * @date Jul 31, 2015
 * This class is used to log some information as required
 * The file name of the log file is defined as the current time
 */
public class Log {
	private static String fileName = Config.logFilePath;
	
	public static void log(String msg) {
		try {
			Date date = new Date();
			String time = date.toLocaleString();
			
			BufferedWriter stdout = new BufferedWriter(new FileWriter(fileName, true));
			stdout.write(time);
			stdout.write("\t");
			stdout.write(msg);
			stdout.newLine();
			
			stdout.flush();
			stdout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String args[]) {
		Log.log("I love you");
	}
}
