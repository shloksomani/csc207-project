package model;

import javafx.scene.image.Image;

import java.io.File;
import java.util.*;

/** Represents a physical image file in a filesystem. */
public abstract class AbsTaggableFile extends Observable implements Observer, Taggable {

  private static final String TAG_MARKER = "@";

  /** the image file in the system */
  private File file;

  /** the previous names for the file */
  private Log log;

  /**
   * Construct a new ImageFile object with a given path.
   *
   * @param path the directory this ImageFile object is under
   */
  public AbsTaggableFile(String path) {
    this(new File(path));
  }

  /**
   * Construct a new ImageFile object representation of a physical file.
   *
   * @param file the physical file for this ImageFile
   */
  public AbsTaggableFile(File file) {
    this.file = file;
    log = new Log(file);
  }

  /**
   * Move the ImageFile to a given directory.
   *
   * @param newPath the string representation of the given directory
   * @return Whether this moving of the ImageFile was successful
   */
  public boolean moveFile(String newPath) {
    File newFile = new File(newPath, file.getName());
    boolean ret1 = file.renameTo(newFile);
    boolean ret2 = log.moveFile(newPath);
    boolean ret = ret1 && ret2;
    if (ret) {
      if (newFile.exists()) {
        file = newFile;
      }
    }
    return ret;
  }

  /**
   * gets all the tags associated to the image file.
   *
   * @return a String[] of associated tags.
   */
  public String[] getTags() {
    return extractTags(getName());
  }

  /** Extracts the Tags in a given string */
  private String[] extractTags(String stringWithTags) {
    List<String> tags = new ArrayList<>();
    String[] slicedString = stringWithTags.split(" ");
    for (String word : slicedString) {
      // Check each word to see if its format indicates it's a tag.
      if (word.length() >= TAG_MARKER.length()
          && word.substring(0, TAG_MARKER.length()).equals(TAG_MARKER)) {
        tags.add(word.substring(1));
      }
    }
    return tags.toArray(new String[tags.size()]);
  }

  /**
   * gets all the tags that were previously associated to the image file.
   *
   * @return a String[] of previously associated tags.
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public String[] getPreviousTags() {
    Set<String> tags = new HashSet<>();
    String[] currentTags = getTags();
    String[] previousNames = log.getColumn(0);
    for (String previousName : previousNames) {
      String[] potentialTags = extractTags(previousName);
      for (String potentialTag : potentialTags) {
        if (!Arrays.asList(currentTags).contains(potentialTag)) {
          tags.add(potentialTag);
        }
      }
    }
    return tags.toArray(new String[tags.size()]);
  }

  /**
   * Tries to add a given tag to the image.
   *
   * @param newTags the tag to try and add.
   * @return true if successful, false if it isn't.
   */
  public boolean addTag(String[] newTags) {
    boolean success = false;
    StringBuilder str = new StringBuilder(getName());
    for (String tag : newTags) {
      if (tag != null
          && tag.length() > 0
          && !Arrays.asList(extractTags(str.toString())).contains(tag)) {
        str.append(String.format(" %s%s", TAG_MARKER, tag));
      }
    }
    if (!str.toString().equals(getName())) {
      try {
        success = rename(str.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return success;
  }

  /**
   * Tries to remove the given tag from the image.
   *
   * @param thisTags the Array of String representations of tags being tried to removed.
   * @return true if removal is successful, false if it isn't.
   */
  public boolean removeTag(String[] thisTags) {
    boolean ret = false;
    String str = getName();
    for (String tag : thisTags) {
      if (tag != null && str.contains(tag) && tag.length() > 0) {
        str = str.replaceAll(String.format(" %s\\b%s\\b", TAG_MARKER, tag), "");
      }
    }
    try {
      ret = rename(str);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * Tries to updateLog the image with the newName
   *
   * @param newName a String to try and updateLog the image with.
   * @return true if renaming is successful, false if it isn't.
   */
  public boolean rename(String newName) throws Exception {
    String lastName = getName();
    File newFile = new File(file.getParent(), newName + getSuffix());
    boolean ret = false;
    if (!newFile.exists() && file.renameTo(newFile)) {
      file = newFile;
      ret = log.updateLog(lastName, newName, file.getName());
    }
    setChanged();
    notifyObservers();
    return ret;
  }

  /**
   * Returns the name of an image with no suffix.
   *
   * @return the name of an image.
   */
  public String getName() {
    String ret = file.getName();
    if (file.getName().lastIndexOf(".") != -1) {
      ret = file.getName().substring(0, file.getName().lastIndexOf("."));
    }
    return ret;
  }

  /**
   * Returns the suffix of an image.
   *
   * @return the suffix of an image
   */
  private String getSuffix() {
    String ret = "";
    if (file.getName().lastIndexOf(".") != -1) {
      ret = file.getName().substring(file.getName().lastIndexOf("."));
    }
    return ret;
  }

  /**
   * Generate a string of the path
   *
   * @return String
   */
  @Override
  public String toString() {
    return file.getAbsolutePath();
  }

  /**
   * Returns the physical Image File.
   *
   * @return the physical Image File.
   */
  public abstract Image getImage();

  /**
   * Returns this file.
   *
   * @return File
   */
  public File getFile() {
    return file;
  }

  /**
   * Checks if object insistence of AbsTaggableFile
   *
   * @param o the object passed in for comparison
   * @return boolean
   */
  @Override
  public boolean equals(Object o) {
    return o != null
        && o instanceof AbsTaggableFile
        && ((AbsTaggableFile) o).getFile().equals(this.getFile());
  }

  /**
   * Update the ImageFile when a tag is deleted from the TagManager.
   *
   * @param o the observable object that updates the observer ImageFile when a Tag is deleted
   * @param arg pass in argument
   */
  @Override
  public void update(Observable o, Object arg) {
    removeTag(((TagManager) o).getLastErasedTag());
  }

  /**
   * Return a list of String representation of tags in the log file.
   *
   * @return A list of String representation of tags in log
   */
  public String[] getLog() {
    return log.getLog();
  }

  /**
   * Returns a specific hash code for a file
   *
   * @return int
   */
  @Override
  public int hashCode() {
    return file.getAbsolutePath().hashCode();
  }
}
