package game.services;

import game.configuration.AppConfiguration;
import game.core.DictionaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manish Shrestha
 */
@Service
class StandardDictionaryManager implements DictionaryManager {
    private final Map<Integer,Map<Character, List<String>>> dictionaryMap = new HashMap<>();

    @Autowired
    public StandardDictionaryManager(AppConfiguration appConfiguration) throws IOException {
        final List<File> extFiles = appConfiguration.getFiles().stream()
                .map(file -> new File(file))
                .collect(Collectors.toList());

        if (extFiles.isEmpty()) {
            final List<File> dictionaries = appConfiguration.getDictionaries().stream()
                    .map(dictionary -> {
                        final DictionaryType dictionaryType = DictionaryType.fromName(dictionary);
                        final File file = new File(StandardDictionaryManager.class.getClassLoader()
                                .getResource(dictionaryType.getFilePath())
                                .getFile());
                        return file;
                    })
                    .collect(Collectors.toList());
            loadDictionaryFiles(dictionaries);
        } else {
            loadDictionaryFiles(extFiles);
        }
    }

    @Override
    public boolean checkWord(String word) {
        return dictionaryMap.getOrDefault(word.length(), Collections.emptyMap())
                .getOrDefault(Character.toLowerCase(word.charAt(0)), Collections.emptyList())
                .stream()
                .anyMatch(checkWord -> checkWord.equalsIgnoreCase(word));
    }

    private void loadDictionaryFiles(List<File> files) throws IOException {
        for (File file: files) {
            final FileReader fileReader = new FileReader(file);
            try (final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    final char startingChar = Character.toLowerCase(line.charAt(0));
                    final int length = line.length();
                    final Map<Character, List<String>> charToWordListMap = dictionaryMap.getOrDefault(length, new HashMap<>());
                    final List<String> wordList = charToWordListMap.getOrDefault(startingChar, new ArrayList<>());
                    wordList.add(line);
                    charToWordListMap.put(startingChar, wordList);
                    dictionaryMap.put(length, charToWordListMap);
                }
            }
        }
    }
}
