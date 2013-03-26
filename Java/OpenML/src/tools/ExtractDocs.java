package tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Extracts info from weka API docs and ARFF files and adds them to the database
 * 
 * @author joa
 */
public class ExtractDocs {

	public Map<String, String> getClassifiers(File dir, String prefix) {
		Map<String, String> classpaths = new HashMap<>();
		File directory = dir;
		File[] fileList = directory.listFiles();
		for (final File file : fileList) {
			if (file.isDirectory()) {
				classpaths.putAll(getClassifiers(file, prefix
						+ file.getName() + "."));
			} else {
				String fileName = file.getName();
				if (fileName.endsWith(".java")) {
					String n = fileName.split(".java")[0];
					classpaths.put(n, prefix + n);
				}
			}
		}
		return classpaths;
	}
	
	public Map<String, String> getDocs(File dir, String prefix) {
		Map<String, String> pages = new HashMap<>();
		File directory = dir;
		File[] fileList = directory.listFiles();
		for (final File file : fileList) {
			if (file.isDirectory()) {
				pages.putAll(getDocs(file, prefix
						+ file.getName() + "/"));
			} else {
				String fileName = file.getName();
				if (fileName.endsWith(".html")) {
					String n = fileName.split(".html")[0];
					pages.put(n, prefix + fileName);
				}
			}
		}
		return pages;
	}
	
	public Map<String, String> getDataDocs(File dir, String prefix) {
		Map<String, String> files = new HashMap<>();
		File directory = dir;
		File[] fileList = directory.listFiles();
		for (final File file : fileList) {
			if (file.isDirectory()) {
				files.putAll(getDataDocs(file, prefix
						+ file.getName() + "/"));
			} else {
				String fileName = file.getName();
				if (fileName.endsWith(".arff")) {
					String n = fileName.split(".arff")[0];
					files.put(n, prefix + fileName);
				}
			}
		}
		return files;
	}

	public static void main(String[] args) {
		ExtractDocs e = new ExtractDocs();
		e.getDataDescriptions();
		e.getImplFullDescriptions();
		e.getImplDescriptions();
	}
	
	public void getImplFullDescriptions(){
		Map<String, String> map = getDocs(new File(
				"data/doc/weka"), "data/doc/weka/");
		try (PrintWriter out = new PrintWriter("implementation_fulldescriptions.sql")) {
			for (String s : map.keySet()) {
				Scanner scanner = new Scanner(new File(map.get(s)));
				boolean active = false;
				outerloop:
				while(scanner.hasNext()){
					String str = scanner.nextLine();
					if(active && str.contains("<!--")){
						out.println("\' where name='weka."+s+"';");
						active = false;
						break outerloop;
					}
					else if(active){
						str = str.replaceAll("\\<.*?>","\n").replace("'", "\\'").replace("\n\n", "\n").replace("\n\n", "\n");
						if(str.startsWith("Class "))
							str = str.replace("Class ","Implementation ");
						if(str.trim().length() > 0 && !str.trim().equals("\n"))
							out.print(str);			
					}
					else if(str.equals("<P>")){
						active=true;
						out.print("UPDATE implementation set fulldescription=\'");
					}
				}
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}
	
	public void getDataDescriptions(){
		Map<String, String> map = getDataDocs(new File(
				"data/data"), "data/data/");
		try (PrintWriter out = new PrintWriter("data_descriptions.sql")) {
			for (String s : map.keySet()) {
				Scanner scanner = new Scanner(new File(map.get(s)));
				out.print("UPDATE dataset set description=\'");
				while(scanner.hasNext()){
					String str = scanner.nextLine();
					if(str.startsWith("%")){
						str = str.substring(1);
						out.println(str.replace("\\","").replace("'","\\'"));
					}
					if(str.startsWith("@")){
						out.println("\' where name='"+s+"';");
						break;
					}
				}
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}
	
	public void getImplDescriptions(){
		Map<String, String> map = getClassifiers(new File(
				"data/weka"), "weka.");
		try (PrintWriter out = new PrintWriter("implementation_descriptions.sql")) {
			for (String s : map.keySet()) {
				String info = getGlobalInfo(map.get(s));
				if (info != null){
					info = info.replace("'", "\\'");
					if(info.startsWith("Class "))
						info = info.replace("Class ","Implementation ");
					out.println("UPDATE implementation set description=\'"+info+"\' where name='weka."+s+"';");
				}
			}
		} catch (IOException exc) {
		}
	}

	public String getGlobalInfo(String classPath) {
		try {
			Object c = Class.forName(classPath).newInstance();
			Method m = c.getClass().getMethod("globalInfo");
			String s = (String) m.invoke(c);
			return s;
		} catch (Exception e) {
			//System.out.printf("%s: no info\n",classPath);
		}
		return null;
	}

}
