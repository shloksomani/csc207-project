package viewer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.AbsTaggableFile;
import model.TaggableFileManager;

import java.io.File;

public class ViewerController {
  /** text to display when there's no image */
  private static final String DEFAULT_IMAGE_NAME = "No Image";

  /** Displays all the current Tags */
  public ListView<String> currentTags;

  /** Displays all the previous Tags */
  public ListView<String> previousTags;

  /** Displays all Tags */
  public ListView<String> directoryTags;

  /** Displays all the lines in log */
  public ListView<String> log;

  /** the main(root) GridPane */
  public BorderPane gp;

  /** the image viewer */
  public ImageView imageView;

  /** Displays the String representations of all images */
  public ListView<AbsTaggableFile> viewer;

  /** Gets the String representation for a new Tag */
  public TextField tagToCreate;

  /** The name of the displayed image */
  public Label imageName;

  /** The imaged displayed as default when there's no image available. */
  public Image defaultImage;

  public ChoiceBox<String> fileType;

  /** the Boolean that indicates which mode it is for the TreeView */
  private boolean toggle;

  /** the taggableFileManager for this GUI */
  private TaggableFileManager taggableFileManager;

  /** the imageFile for this GUI */
  private AbsTaggableFile selectedImageFile;

  /** the observable list of currentTags */
  private ObservableList<String> currentTagsList;

  /** the observable list of directoryTags */
  private ObservableList<String> directoryTagsList;

  /** the observable list of viewer */
  private ObservableList<String> previousTagsList;

  /** the observable list of viewer */
  private ObservableList<AbsTaggableFile> viewerList;

  /** the observable list of log */
  private ObservableList<String> logList;

  /** Construct a ViewerController. */
  public ViewerController() {
    toggle = false;
    taggableFileManager = new TaggableFileManager("");
    currentTagsList = FXCollections.observableArrayList();
    directoryTagsList = FXCollections.observableArrayList();
    previousTagsList = FXCollections.observableArrayList();
    viewerList = FXCollections.observableArrayList();
    logList = FXCollections.observableArrayList();
  }

  /**
   * Sets up the GUI before the directory is selected.
   *
   * @param stage the initial stage of the GUI
   */
  @FXML
  void setup(Stage stage) {
    changeDirectory(stage);
    currentTags.setItems(currentTagsList);
    currentTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    directoryTags.setItems(directoryTagsList);
    directoryTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    previousTags.setItems(previousTagsList);
    previousTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    viewer.setItems(viewerList);
    log.setItems(logList);
    fileType.getItems().add("Image");
    fileType.getItems().add("Text");
    fileType.getItems().add("Audio");
    fileType.setValue("Image");
    fileType
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> updateAll());
    updateAll();
  }

  /**
   * Ask the viewer for a new directory to change directory.
   *
   * @param window this window
   */
  private void changeDirectory(Window window) {
    DirectoryChooser dc = new DirectoryChooser();
    File initialDirectory = new File(System.getProperty("user.home"));
    File newDirectory;

    if (taggableFileManager.getRoot().exists()) {
      initialDirectory = taggableFileManager.getRoot();
    }

    dc.setInitialDirectory(initialDirectory);

    do {
      newDirectory = dc.showDialog(window);
      if (newDirectory != null && newDirectory.exists()) {
        taggableFileManager.changeDirectory(newDirectory);
        selectedImageFile = null;
        updateAll();
      }
    } while (newDirectory != null && !newDirectory.exists());
  }

  /**
   * Ask the viewer for a new directory to move file.
   *
   * @param window this window
   */
  @FXML
  private void moveFile(Window window) {
    DirectoryChooser dc = new DirectoryChooser();
    File initialDirectory = new File(System.getProperty("user.home"));
    File newDirectory;

    if (taggableFileManager.getRoot().exists()) {
      initialDirectory = taggableFileManager.getRoot();
    }

    dc.setInitialDirectory(initialDirectory);

    do {
      newDirectory = dc.showDialog(window);
      if (newDirectory != null && newDirectory.exists()) {
        selectedImageFile.moveFile(newDirectory.getPath());
        selectedImageFile = null;
        updateAll();
      }
    } while (newDirectory != null && !newDirectory.exists());
  }

  // Start of all the Update methods.

  /** Update everything. */
  @FXML
  private void updateAll() {
    updateImageFileManagerViews();
    updateImageFileViews();
  }

  /** Updates all the TaggableFileManager related views. */
  @FXML
  private void updateImageFileManagerViews() {
    viewerList.clear();
    directoryTagsList.clear();
    if (taggableFileManager != null) {
      AbsTaggableFile[] imageFiles;
      imageFiles = taggableFileManager.getTaggableFiles(fileType.getValue(), toggle);
      viewerList.addAll(imageFiles);
      if (selectedImageFile != null) {
        viewer.getSelectionModel().select(selectedImageFile);
      }
      directoryTagsList.addAll(taggableFileManager.getAllCurrentTags());
    }
  }

  /** Updates all the ImageFile related views */
  @FXML
  private void updateImageFileViews() {
    // Clear everything that is to be updated
    currentTagsList.clear();
    previousTagsList.clear();
    logList.clear();
    imageName.setText(DEFAULT_IMAGE_NAME);
    imageView.setImage(defaultImage);

    // If there's a selected ImageFile then update the views with the new ImageFile related
    // material.
    if (selectedImageFile != null) {
      // Update the list of tags currently assigned to the Image
      currentTagsList.addAll(selectedImageFile.getTags());
      // Update the list of all the previous tags that were assigned to the Image
      previousTagsList.addAll(selectedImageFile.getPreviousTags());
      // Update the log of all the changes to the Image
      for (String logEntry : selectedImageFile.getLog()) {
        logList.add(logEntry.replaceFirst("/", "->").replaceFirst("/", "|"));
      }
      // Update the name of the Image.
      imageName.setText(selectedImageFile.getName());
      // Update the
      imageView.setImage(selectedImageFile.getImage());
    }
    // keep the log up to date
    log.scrollTo(logList.size() - 1);
    // Resize the imageView to match the given space.
    imageView.autosize();
  }

  // End of all the Update methods.
  // Start of all the handle methods.

  /** Handles the viewer click action. */
  @FXML
  public void handleViewerClick() {
    AbsTaggableFile imageFile = viewer.getSelectionModel().getSelectedItem();
    if (imageFile != null) {
      selectedImageFile = imageFile;
      updateAll();
    }
  }

  /** Handles the toggle view action. */
  @FXML
  public void handleToggleViewerAction() {
    toggle = !toggle;
    updateImageFileManagerViews();
  }

  /** Handles the change directory action. */
  @FXML
  public void handleChangeDir() {
    changeDirectory(gp.getScene().getWindow());
  }

  /** Handles the add Tag action. */
  @FXML
  public void handleAddTag() {
    if (selectedImageFile != null) {
      if (selectedImageFile.addTag(
          directoryTags
              .getSelectionModel()
              .getSelectedItems()
              .toArray(new String[directoryTags.getSelectionModel().getSelectedItems().size()]))) {
        updateAll();
      }
    }
  }

  /** Handles the restore Tag action. */
  @FXML
  public void handleRestoreTag() {
    if (selectedImageFile != null) {
      if (selectedImageFile.addTag(
          previousTags
              .getSelectionModel()
              .getSelectedItems()
              .toArray(new String[previousTags.getSelectionModel().getSelectedItems().size()]))) {
        updateAll();
      }
    }
  }

  /** Handles the create Tag action. */
  @FXML
  public void handleCreateTag() {
    if (selectedImageFile != null) {
      if (taggableFileManager.addTag(new String[] {tagToCreate.getText()})) {
        tagToCreate.clear();
        updateAll();
      }
    }
  }

  /** Handles the move file action. */
  @FXML
  public void handleMoveFile() {
    moveFile(gp.getScene().getWindow());
  }

  /** Handles the remove Tag action. */
  @FXML
  public void handleRemoveTag() {
    if (selectedImageFile != null) {
      if (selectedImageFile.removeTag(
          currentTags
              .getSelectionModel()
              .getSelectedItems()
              .toArray(new String[currentTags.getSelectionModel().getSelectedItems().size()]))) {
        updateAll();
      }
    }
  }

  /** Handles the delete Tag action. */
  @FXML
  public void handleDeleteTag() {
    if (taggableFileManager != null) {
      if (taggableFileManager.deleteTag(
          directoryTags
              .getSelectionModel()
              .getSelectedItems()
              .toArray(new String[directoryTags.getSelectionModel().getSelectedItems().size()]))) {
        updateAll();
      }
    }
  }

  /**
   * Handles if the user enters the Enter key in the text input.
   *
   * @param keyEvent key pressed
   */
  @FXML
  public void handleKeyPressed(KeyEvent keyEvent) {
    if (keyEvent.getCode() == KeyCode.ENTER) {
      handleCreateTag();
    }
  }

  /** Restore the previous state */
  @FXML
  public void handleRestorePreviousState() {
    try {
      String newName = "";
      if (log.getSelectionModel().getSelectedItem() != null) {
        String selectedItem = log.getSelectionModel().getSelectedItem();
        newName = selectedItem.substring(0, selectedItem.indexOf(" -> "));
      }
      if (selectedImageFile != null && !newName.equals("") && selectedImageFile.rename(newName)) {
        updateAll();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // End of all the handle methods.
}
