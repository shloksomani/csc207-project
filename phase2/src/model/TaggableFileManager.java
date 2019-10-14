package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Manages all the imageFiles under a root folder */
public class TaggableFileManager {
  /** String to match all image files */
  private static final String IMAGE_FILE = "^.*[.](jpg|JPG|jpeg|JPEG|png|PNG|gif|GIF|bmp|BMP)$";

  private static final String TEXT_FILE =
      "^.*[.](txt|TXT|doc|DOC|docx|DOCX|odt|ODT|pdf|PDF|rtf|RTF|tex|TEX)$";
  private static final String AUDIO_FILE =
      "^.*[.](aif|AIF|cda|CDA|mid|MID|midi|MIDI|mp3|MP3|mp4|MP4|mpa|MPA|ogg|OGG|wav|WAV|wma|WMA|wpl|WPL)$";

  /** the root of the directory */
  private File root;

  /** the tagManager */
  private TagManager tagManager;

  /** a set of AbsTaggableFiles */
  private Set<AbsTaggableFile> absTaggableFiles;

  /**
   * Construct a new TaggableFileManager object.
   *
   * @param path the root directory for this TaggableFileManager object
   */
  public TaggableFileManager(String path) {
    this(new File(path));
  }

  /**
   * Construct a new TaggableFileManager object.
   *
   * @param file file for this TaggableFileManager object
   */
  TaggableFileManager(File file) {
    root = new File("");
    tagManager = new TagManager();
    absTaggableFiles = new HashSet<>();
    changeDirectory(file);
  }

  /**
   * Returns all the image files anywhere under the root directory.
   *
   * @param fileType file type to return, must be either "Image","Audio",or "Text"
   * @param toggle true if recursively get all taggable files, false if just local
   * @return a AbsTaggableFile[] of all files that match the fileType and recursively under the root
   *     if the toggle is true. Directly under the root if false.
   */
  public AbsTaggableFile[] getTaggableFiles(String fileType, boolean toggle) {
    String regex = getRegEx(fileType);
    List<File> matchingFiles = new ArrayList<>();
    if (toggle) {
      if (root.isDirectory() || (root.isFile() && root.getName().matches(regex))) {
        matchingFiles.add(root);

        int i = 0;
        while (i < matchingFiles.size()) {
          // Check if element at i in ret is a Directory, a File, or it exists.
          if (matchingFiles.get(i).isDirectory()) {
            // Element at i is a directory, so determine if it has children and remove the Element
            // at
            // i.
            // Check if the Element at i has children.
            if (matchingFiles.get(i).list() != null) {
              // Element at i has children, so add children to the ArrayList if they match the
              // regEx.
              for (File file : matchingFiles.get(i).listFiles()) {
                if (file.getName().matches(regex) || file.isDirectory()) {
                  matchingFiles.add(file);
                }
              }
            }
            matchingFiles.remove(i);
          } else if (matchingFiles.get(i).isFile()) {
            // Element at i is a File, so increment i by 1.
            i += 1;
          } else {
            // Element doesn't exist in filesystem, so remove from ret.
            matchingFiles.remove(i);
          }
        }
      }
    } else {
      if (root.isFile() && root.getName().matches(regex)) {
        matchingFiles.add(root);
      }
      if (root.list() != null) {
        for (File file : root.listFiles()) {
          if (file.isFile() && file.getName().matches(regex)) {
            matchingFiles.add(file);
          }
        }
      }
    }
    return generateAbsTaggableFiles(matchingFiles);
  }

  /**
   * Returns the regex according to the type of the file.
   * @param fileType the String representation of the type of this file
   * @return String
   */
  private String getRegEx(String fileType) {
    String regex = "";
    if (fileType != null) {
      switch (fileType) {
        case "Image":
          regex = IMAGE_FILE;
          break;
        case "Text":
          regex = TEXT_FILE;
          break;
        case "Audio":
          regex = AUDIO_FILE;
          break;
      }
    }
    return regex;
  }

  /**
   * Return an array of the file in list
   * @param files A list of files to generate
   * @return a AbsTaggableFile[] array
   */
  private AbsTaggableFile[] generateAbsTaggableFiles(List<File> files) {
    List<AbsTaggableFile> possibleAbsTaggableFiles = new ArrayList<>(files.size());
    for (File file : files) {
      if (file.getName().matches(IMAGE_FILE)) {
        possibleAbsTaggableFiles.add(new ImageFile(file));
      } else {
        possibleAbsTaggableFiles.add(new GeneralFile(file));
      }
    }
    Set<AbsTaggableFile> newAbsTaggableFiles =
        new HashSet<>(possibleAbsTaggableFiles.size() + absTaggableFiles.size());
    newAbsTaggableFiles.addAll(absTaggableFiles);
    newAbsTaggableFiles.addAll(possibleAbsTaggableFiles);
    newAbsTaggableFiles.retainAll(possibleAbsTaggableFiles);
    tagManager.deleteObservers();
    for (AbsTaggableFile absTaggableFile : newAbsTaggableFiles) {
      try {
        tagManager.addTag(absTaggableFile.getTags());
      } catch (Exception e) {
        e.printStackTrace();
      }
      absTaggableFile.addObserver(tagManager);
      tagManager.addObserver(absTaggableFile);
    }
    absTaggableFiles = newAbsTaggableFiles;
    return absTaggableFiles.toArray(new AbsTaggableFile[absTaggableFiles.size()]);
  }

  /**
   * returns all the current existing tags, associated or unassociated to images
   *
   * @return a String[] of all the tags.
   */
  public String[] getAllCurrentTags() {
    return tagManager.getTags();
  }

  /**
   * Deletes Tag from all files and tagManager
   *
   * @param tags the Array of String representations of tags to delete
   * @return true if it succeeds, false if it doesn't.
   */
  public boolean deleteTag(String[] tags) {
    try {
      return tagManager.removeTag(tags);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Add Tag from all files and tagManager
   *
   * @param tags the Array of String representations of tags to delete
   * @return true if it succeeds, false if it doesn't.
   */
  public boolean addTag(String[] tags) {
    try {
      return tagManager.addTag(tags);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Changes the working directory of TaggableFileManager if new directory exists. All tags that
   * aren't associated with an Image will when be unavailable when switch happens unless restored.
   *
   * @param path the String path of the root folder to try to switch to
   * @return true if changingDirectory succeeds.
   */
  public boolean changeDirectory(String path) {
    return changeDirectory(new File(path));
  }

  /**
   * Changes the working directory of TaggableFileManager if new directory exists. All tags that
   * aren't associated with an Image will when be unavailable when switch happens unless restored.
   *
   * @param root the root folder to try to switch to
   * @return true if changingDirectory succeeds.
   */
  public boolean changeDirectory(File root) {
    boolean ret = false;
    if (root.exists()) {
      ret = true;
      this.root = root;
    }
    return ret;
  }

  /**
   * Returns the root
   * @return File
   */
  public File getRoot() {
    return root;
  }
}
