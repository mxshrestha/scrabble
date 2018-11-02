package game.services;

import game.configuration.AppConfiguration;
import game.core.DictionaryType;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Manish Shrestha
 */
public class StandardDictionaryManagerTest {

    @Test
    public void testDefaultFileLoadToMap() throws IOException {
        final List<String> dictionaries = Lists.newArrayList("SOWPODS", "TWL06");
        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.setDictionaries(dictionaries);

        StandardDictionaryManager standardDictionaryManager = new StandardDictionaryManager(appConfiguration);

        List<String> wordList = readDictionaries(dictionaries);

        for (String word: wordList) {
            Assert.assertTrue(standardDictionaryManager.checkWord(word));
        }
    }

    @Test
    public void testExternalFileLoadToMap() throws IOException {
        final List<String> dictionaries = Lists.newArrayList("SOWPODS", "TWL06");
        final ArrayList<String> extFiles = Lists.newArrayList(StandardDictionaryManagerTest.class.getClassLoader().getResource("SOWPODS_TEST.txt").getPath(),
                StandardDictionaryManagerTest.class.getClassLoader().getResource("TWL06_TEST.txt").getPath());

        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.setDictionaries(dictionaries);
        appConfiguration.setFiles(extFiles);

        StandardDictionaryManager standardDictionaryManager = new StandardDictionaryManager(appConfiguration);

        Assert.assertTrue(standardDictionaryManager.checkWord("HUMAN_TEST"));
        Assert.assertTrue(standardDictionaryManager.checkWord("ANIMAL_TEST"));
        Assert.assertTrue(standardDictionaryManager.checkWord("CAT_TEST"));
        Assert.assertTrue(standardDictionaryManager.checkWord("DOG_TEST"));

        List<String> wordList = readDictionaries(dictionaries);
        for (String word: wordList) {
            Assert.assertFalse(standardDictionaryManager.checkWord(word));
        }
    }

    @Test
    public void testCheckWordWhenWordDoesNotExists() throws IOException {
        final List<String> dictionaries = Lists.newArrayList("SOWPODS", "TWL06");

        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.setDictionaries(dictionaries);

        StandardDictionaryManager standardDictionaryManager = new StandardDictionaryManager(appConfiguration);

        Assert.assertFalse(standardDictionaryManager.checkWord("ABSLRE"));
    }

    @Test
    public void testCheckWordWhenWordExists() throws IOException {
        final List<String> dictionaries = Lists.newArrayList("SOWPODS", "TWL06");
        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.setDictionaries(dictionaries);

        StandardDictionaryManager standardDictionaryManager = new StandardDictionaryManager(appConfiguration);

        Assert.assertTrue(standardDictionaryManager.checkWord("CAT"));
    }

    private List<String> readDictionaries(List<String> dictionaries) throws IOException {
        List<String> words = new ArrayList<>();
        final List<File> fileList = dictionaries.stream()
                .map(dictionary -> {
                    final DictionaryType dictionaryType = DictionaryType.fromName(dictionary);
                    final File file = new File(StandardDictionaryManager.class.getClassLoader()
                            .getResource(dictionaryType.getFilePath())
                            .getFile());
                    return file;
                })
                .collect(Collectors.toList());

        for (File file: fileList) {
            final FileReader fileReader = new FileReader(file);
            try (final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    words.add(line);
                }
            }
        }
        return words;
    }
}
