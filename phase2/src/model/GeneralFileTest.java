package model;

import javafx.scene.image.Image;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GeneralFileTest {

  private final String TEST_FILE_NAME = "testFile";
  @Rule public TemporaryFolder folder = new TemporaryFolder();
  private GeneralFile generalFile;
  private File testFile;

  @Before
  public void setUp() throws Exception {
    testFile = folder.newFile(TEST_FILE_NAME);
    generalFile = new GeneralFile(testFile);
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void getImage() throws Exception {
    Image expectedResult = null;
    assertEquals(expectedResult, generalFile.getImage());
  }

  @Test
  public void moveFile() throws Exception {
    File aFolder = folder.newFolder("aFolder");
    generalFile.moveFile(aFolder.getPath());
    File expectedResult = new File(aFolder.getPath(), TEST_FILE_NAME);
    File expectedResult1 = new File(aFolder.getPath(), "." + TEST_FILE_NAME + ".log");
    assertTrue(expectedResult.exists());
    assertTrue(expectedResult1.exists());
  }

  @Test
  public void getTagsWithNoTags() throws Exception {
    String[] expectedResult = new String[0];
    Assert.assertArrayEquals(expectedResult, generalFile.getTags());
  }

  @Test
  public void getTagsWithATag() throws Exception {
    File fileWithTag = folder.newFile("fileName @tag");
    generalFile = new GeneralFile(fileWithTag);
    String[] expectedResult = new String[] {"tag"};
    Assert.assertArrayEquals(expectedResult, generalFile.getTags());
  }

  @Test
  public void getTagsWithMultipleTags() throws Exception {
    File fileWithTags = folder.newFile("fileName @tag @tag2 @tag3");
    generalFile = new GeneralFile(fileWithTags);
    String[] expectedResult = new String[] {"tag", "tag2", "tag3"};
    Assert.assertArrayEquals(expectedResult, generalFile.getTags());
  }

  @Test
  public void getPreviousTagsWithNoPreviousTags() throws Exception {
    String[] expectedResult = new String[0];
    Assert.assertArrayEquals(expectedResult, generalFile.getPreviousTags());
  }

  @Test
  public void getPreviousTagsWithAPreviousTagAndNoCurrent() throws Exception {
    File logFile = new File(testFile.getParentFile(), "." + testFile.getName() + ".log");
    BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
    writer.append("fileName @tag / fileName / time");
    writer.close();
  }

  @Test
  public void getPreviousTagsWithAnIdencticalPreviousTagAndCurrent() throws Exception {
    File fileWithOverlappingTag = folder.newFile("fileName @tag");
    generalFile = new GeneralFile(fileWithOverlappingTag);
    File logFile =
        new File(testFile.getParentFile(), "." + fileWithOverlappingTag.getName() + ".log");
    BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
    writer.append("fileName @tag / fileName / time");
    writer.close();
    String[] expectedResults = new String[0];
    Assert.assertArrayEquals(expectedResults, generalFile.getPreviousTags());
  }

  @Test
  public void getPreviousTagsWithMultipleTagsAndNoCurrent() throws Exception {
    File logFile = new File(testFile.getParentFile(), "." + testFile.getName() + ".log");
    BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
    writer.append("fileName @tag @tag2 @tag3 / fileName / time");
    writer.close();
    String[] expectedResults = new String[] {"tag", "tag2", "tag3"};
    Assert.assertArrayEquals(expectedResults, generalFile.getPreviousTags());
  }

  @Test
  public void getPreviousTagsWithMultipleTagsAndSomeIdenticalCurrent() throws Exception {
    File fileWithOverlappingTags = folder.newFile("fileName @tag @tag2");
    generalFile = new GeneralFile(fileWithOverlappingTags);
    File logFile =
        new File(testFile.getParentFile(), "." + fileWithOverlappingTags.getName() + ".log");
    BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
    writer.append("fileName @tag @tag2 @tag3 / fileName / time");
    writer.close();
    String[] expectedResults = new String[] {"tag3"};
    Assert.assertArrayEquals(expectedResults, generalFile.getPreviousTags());
  }

  @Test
  public void addTag() throws Exception {
    generalFile.addTag(new String[] {"Tag"});
    File expectedResults = new File(folder.getRoot(), TEST_FILE_NAME + " @Tag");
    File shouldntExist = testFile;
    assertTrue(expectedResults.exists());
    assertFalse(shouldntExist.exists());
  }

  @Test
  public void removeTag() throws Exception {
    File fileWithTag = folder.newFile("fileName @tag");
    generalFile = new GeneralFile(fileWithTag);
    generalFile.removeTag(new String[] {"tag"});
    File expectedResults = new File(folder.getRoot(), "fileName");
    File shouldntExist = fileWithTag;
    assertTrue(expectedResults.exists());
    assertFalse(shouldntExist.exists());
  }

  @Test
  public void rename() throws Exception {
    generalFile.rename("newName");
    File expectedResults = new File(folder.getRoot(), "newName");
    File shouldntExist = testFile;
    assertTrue(expectedResults.exists());
    assertFalse(shouldntExist.exists());
  }

  @Test
  public void getName() throws Exception {
    String expectedResult = testFile.getName();
    assertEquals(expectedResult, generalFile.getName());
  }

  @Test
  public void testToString() throws Exception {
    String expectedResult = testFile.getAbsolutePath();
    assertEquals(expectedResult, generalFile.toString());
  }

  @Test
  public void getFile() throws Exception {
    File expectedResult = testFile;
    assertEquals(expectedResult, generalFile.getFile());
  }

  @Test
  public void equals() throws Exception {
    GeneralFile expectedResult = new GeneralFile(testFile);
    assertEquals(expectedResult, generalFile);
  }

  @Test
  public void update() throws Exception {
    File fileWithTag = folder.newFile("fileName @tag");
    generalFile = new GeneralFile(fileWithTag);
    TagManager tagManager =
        new TagManager() {
          @Override
          public String[] getLastErasedTag() {
            return new String[] {"tag"};
          }
        };
    generalFile.update(tagManager, null);
    File expectedResult = new File(folder.getRoot(), "fileName");
    File shouldntExist = fileWithTag;
    assertTrue(expectedResult.exists());
    assertFalse(shouldntExist.exists());
  }

  @Test
  public void testHashCode() throws Exception {
    int expectedResult = testFile.getAbsolutePath().hashCode();
    assertEquals(expectedResult, generalFile.hashCode());
  }
}
