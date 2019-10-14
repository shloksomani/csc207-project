package model;

/** Represents all classes that can have tags added and removed from it */
public interface Taggable {

  /**
   * Add tags to the file
   * @param tags List of String representations of tags
   * @return boolean
   * @throws Exception throw exception when somehow the adding can not be complete
   */
  boolean addTag(String[] tags) throws Exception;

  /**
   * removes tag from a given file
   * @param tags List of String representations of tags
   * @return boolean
   * @throws Exception throw exception when somehow the removal can not be complete
   */
  boolean removeTag(String[] tags) throws Exception;

  String[] getTags();
}
