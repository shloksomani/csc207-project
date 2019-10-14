package model;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/** Manages the log file for the target class (ImageFile and TagManager in our model). */
public class Log {

  private static final String LOG_FILE_SUFFIX = ".log";
  private static final String LOG_FILE_SEPARATOR = " / ";
  private static final String LOG_FILE_PREFIX = ".";
  /** the log file for the target class. */
  private File log;

  /**
   * Creates a log file for a file
   *
   * @param file the physical file this log file is created for.
   */
  public Log(File file) {
    log = new File(file.getParent(), LOG_FILE_PREFIX + file.getName() + LOG_FILE_SUFFIX);
    if (!log.exists()) {
      try {
        log.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (System.getProperty("os.name").contains("Windows")) {
      Path logPath =
          FileSystems.getDefault().getPath(log.getParentFile().getAbsolutePath(), log.getName());
      try {
        Files.setAttribute(logPath, "dos:hidden", true);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Constructs a new log file at the given root with the given name having the needed prefix and
   * suffix added to it.
   *
   * @param fileName the name of the log file
   */
  public Log(String root, String fileName) {
    this(new File(root, fileName));
  }

  /**
   * When the file is moved, move this log file as well.
   *
   * @param newPath the String representation of the given directory
   * @return Whether this moving the files was successful
   */
  boolean moveFile(String newPath) {
    File newLog = new File(newPath, log.getName());
    //    log = renameFile(newLog);
    boolean ret = log.renameTo(newLog);
    assert ret;
    log = newLog;
    return true;
  }

  /**
   * Add the new line into the now log file.
   *
   * @param entry1 the first entry to add to the log
   * @param entry2 the second entry to add to the log
   * @throws Exception exception is thrown if unable to write to the log file
   */
  private void addEntry(String entry1, String entry2) throws Exception {
    // Add the new line into the now log file.
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));
      writer.append(generateLogEntry(entry1, entry2));
      writer.flush();
      writer.close();
    } catch (IOException ex) {
      //      ex.printStackTrace();
      throw new Exception("Unable to write to Log file: " + ex.getMessage());
    }
  }

  /**
   * Helps generate the Log entry
   *
   * @param entry1 the first entry in the log
   * @param entry2 the second entry in the log
   * @return returns the format of a row in the log
   */
  private String generateLogEntry(String entry1, String entry2) {
    return String.format(
        "%s%s%s%s%tD %tT\n",
        entry1,
        LOG_FILE_SEPARATOR,
        entry2,
        LOG_FILE_SEPARATOR,
        Calendar.getInstance(),
        Calendar.getInstance());
  }

  /**
   * Record the renaming of the target file in the log file.
   *
   * @param entry1 the last name of the target file
   * @param entry2 the new name of the target file
   * @param newLogName the new log file
   * @return the log file after recording the renaming
   */
  boolean updateLog(String entry1, String entry2, String newLogName) throws Exception {
    File newLog =
        new File(
            log.getParentFile(),
            String.format("%s%s%s", LOG_FILE_PREFIX, newLogName, LOG_FILE_SUFFIX));
    boolean ret = log.renameTo(newLog);
    assert ret;
    log = newLog;
    addEntry(entry1, entry2);
    return true;
  }

  /**
   * Update the log
   *
   * @param entry1 the first entry to add to the log.
   * @param entry2 the second entry to add to the log.
   * @throws Exception throws exception if unable to update log.
   */
  void updateLog(String entry1, String entry2) throws Exception {
    addEntry(entry1, entry2);
  }

  /**
   * Return a list of String representations of the recording of the tag changes in the log file. a
   * String in the String[] has the following format:
   *
   * <p>"firstData / secondData / MM/DD/YY HH:MM:SS"
   *
   * @return the list of String representations
   */
  String[] getLog() {
    List<String> logs = new ArrayList<>();
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(log.getPath()));
      String line = reader.readLine();
      while (line != null) {
        logs.add(line);
        line = reader.readLine();
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return logs.toArray(new String[logs.size()]);
  }

  /**
   * Returns the column of entries at the given number between 0-2.
   *
   * @param column the column of data to get, should be a value between 0-2 inclusive.
   */
  String[] getColumn(int column) {
    List<List<String>> tabledLog = new ArrayList<>();
    List<String> ret = new ArrayList<>();
    for (String logEntry : getLog()) {
      List<String> inner = new ArrayList<>();
      inner.addAll(Arrays.asList(logEntry.split(LOG_FILE_SEPARATOR)));
      tabledLog.add(inner);
    }
    for (List<String> row : tabledLog) {
      ret.add(row.get(column));
    }
    return ret.toArray(new String[ret.size()]);
  }
}
