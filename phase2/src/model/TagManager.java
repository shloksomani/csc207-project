package model;

import java.util.*;

/** manages a collection of tags. */
public class TagManager extends Observable implements Observer, Taggable {
  private static final String LOG_FILE_NAME = "TagManager";
  /** A set of tags. */
  private Set<String> tags;
  /** A Log file */
  private Log log;

  /** A String Array of erased tags */
  private String[] lastErasedTags;

  /** Construct a new TagManager with no existing tag. */
  public TagManager() {
    lastErasedTags = new String[0];
    tags = new HashSet<>();
    log = new Log(".", LOG_FILE_NAME);
    // Get the tags of the previous session of the program.
    String[] column1 = log.getColumn(1);
    String[] tagSet = new String[0];
    if (column1.length > 0) {
      tagSet =
          (column1[column1.length - 1].replaceFirst("\\[", "").replaceFirst("]", "").split(","));
    }
    for (String s : tagSet) {
      s = s.replaceFirst("\\[", "");
      s = s.replaceFirst("]", "");
      s = s.trim();
      if (s.length() > 0) {
        tags.add(s);
      }
    }
  }

  /**
   * Construct a new TagManager with a list of existing tags.
   *
   * @param tags a list of string representations for existing tags.
   */
  public TagManager(String[] tags) {
    this.tags = new HashSet<>(Arrays.asList(tags));
  }

  /**
   * Add a new tag to the TagManager.
   *
   * @param newTags the Array of the string representations for new Tags to be added
   * @return a boolean indicating whether the adding of this tag succeeded
   */
  public boolean addTag(String[] newTags) throws Exception {
    Set<String> oldSet = new HashSet<>(tags);
    boolean success = false;
    for (String tag : newTags) {
      success = tags.add(tag);
    }
    if (success) {
      setChanged();
      notifyObservers();
      log.updateLog(oldSet.toString(), tags.toString());
    }
    return success;
  }

  /**
   * Remove a tag from the TagManager.
   *
   * @param tags the Array of string representations for Tags to be removed
   * @return a boolean indicating whether the removal of this tag succeeded
   */
  public boolean removeTag(String[] tags) throws Exception {
    Set<String> oldSet = new HashSet<>(this.tags);
    boolean success = false;
    for (String tag : tags) {
      success = this.tags.remove(tag);
    }
    if (success) {
      lastErasedTags = tags.clone();
      setChanged();
      notifyObservers();
      log.updateLog(oldSet.toString(), this.tags.toString());
    }
    return success;
  }

  /**
   * Return existing tags.
   *
   * @return a String[] of the existing tags
   */
  public String[] getTags() {
    return tags.toArray(new String[tags.size()]);
  }

  /**
   * Update the tagManager with new tags.
   *
   * @param o the observable object that updates the observer TagManager when new Tag is added
   * @param arg pass in argument
   */
  @Override
  public void update(Observable o, Object arg) {
    try {
      addTag(((AbsTaggableFile) o).getTags());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Return last Erased Tags
   *
   * @return String[]
   */
  String[] getLastErasedTag() {
    return lastErasedTags;
  }

  //  public Set<String> getPreviousGlobalTags() {
  //    return log.getPreviousGlobalTags();
  //  }
}
