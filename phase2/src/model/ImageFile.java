package model;

import javafx.scene.image.Image;

import java.io.File;

/** Represents a physical image file in a filesystem. */
public class ImageFile extends AbsTaggableFile {

  /**
   * Construct a new ImageFile object with a given path.
   *
   * @param path the directory this ImageFile object is under
   */
  public ImageFile(String path) {
    this(new File(path));
  }

  /**
   * Construct a new ImageFile object representation of a physical file.
   *
   * @param file the physical file for this ImageFile
   */
  public ImageFile(File file) {
    super(file);
  }

  /**
   * Returns the physical Image File.
   *
   * @return the physical Image File.
   */
  public Image getImage() {
    return new Image("file:" + super.getFile().getAbsolutePath());
  }
}
