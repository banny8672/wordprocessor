package com.nokia.wordprocessor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileDeleteStrategy;

public class FolderZiper {

	public static void main(String[] args) throws Exception {
		String INPUT_DIR = "D:\\userdata\\savaliya\\Desktop\\Zip\\outp\\";
		String OUTPUT_FILE = "D:\\userdata\\savaliya\\Desktop\\Zip";
		zipDirectory(new File(INPUT_DIR), new File(OUTPUT_FILE), "output_crn");
		System.out.println("Done");
	}

	/*public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName)
			throws Exception {
		if (fileToZip == null || !fileToZip.exists()) {
			return;
		}

		String zipEntryName = fileToZip.getName();
		if (parrentDirectoryName != null && !parrentDirectoryName.isEmpty()) {
			zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
		}

		if (fileToZip.isDirectory()) {
			System.out.println("+" + zipEntryName);
			for (File file : fileToZip.listFiles()) {
				addDirToZipArchive(zos, file, zipEntryName);
			}
		} else {
			System.out.println("   " + zipEntryName);
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(fileToZip);
			zos.putNextEntry(new ZipEntry(zipEntryName));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
	}
*/
	/**
	 * Compress a directory to ZIP file including subdirectories
	 * 
	 * @param directoryToCompress
	 *            directory to zip
	 * @param outputDirectory
	 *            where to place the compress file
	 */
	public static void zipDirectory(File directoryToCompress, File outputDirectory, String outputFileName) {
		try {
			FileOutputStream dest = new FileOutputStream(
					new File(outputDirectory, outputFileName + ".doc"));
			ZipOutputStream zipOutputStream = new ZipOutputStream(dest);

			zipDirectoryHelper(directoryToCompress, directoryToCompress, zipOutputStream);
			zipOutputStream.close();
			System.out.println("BANNY "+directoryToCompress.getAbsolutePath());
			FileDeleteStrategy.FORCE.delete(directoryToCompress);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void zipDirectoryHelper(File rootDirectory, File currentDirectory, ZipOutputStream out)
			throws Exception {
		byte[] data = new byte[2048];

		File[] files = currentDirectory.listFiles();
		if (files == null) {
			// no files were found or this is not a directory

		} else {
			for (File file : files) {
				if (file.isDirectory()) {
					zipDirectoryHelper(rootDirectory, file, out);
				} else {
					FileInputStream fi = new FileInputStream(file);
					// creating structure and avoiding duplicate file names
					String name = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath(), "");
					// String name = file.getCanonicalPath();
					System.out.println(rootDirectory.getAbsolutePath() + " ---> " + name.substring(1));
					ZipEntry entry = new ZipEntry(name.substring(1));
					out.putNextEntry(entry);
					int count;
					BufferedInputStream origin = new BufferedInputStream(fi, 2048);
					while ((count = origin.read(data, 0, 2048)) != -1) {
						out.write(data, 0, count);
					}
					origin.close();
				}
			}
		}
	}
}
