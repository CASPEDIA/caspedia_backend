package com.cast.caspedia.boardgame.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoardgameCsvSaveService {

    private static final int BATCH_SIZE = 50; // 배치 사이즈 설정

    @Autowired
    private BoardgameRepository repository;

    @Transactional
    public void importCsvData(String csvFilePath) {
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
            String[] nextLine;
            List<Boardgame> entities = new ArrayList<>();
            csvReader.readNext(); // 헤더 건너뛰기

            while ((nextLine = csvReader.readNext()) != null) {
                Integer boardgameKey = Integer.valueOf(nextLine[0]); // boardgame_key
                String nameEng = nextLine[1]; // name_eng
                int yearPublished = Integer.parseInt(nextLine[2]); // year_published

                // 기존 데이터 존재 여부 확인
                Boardgame entity = repository.findById(boardgameKey).orElse(new Boardgame());
                entity.setBoardgameKey(boardgameKey);
                entity.setNameEng(nameEng);
                entity.setYearPublished(yearPublished);

                entities.add(entity);

                // 배치 사이즈만큼 모이면 데이터베이스에 저장하고 리스트 초기화
                if (entities.size() == BATCH_SIZE) {
                    repository.saveAll(entities);
                    entities.clear(); // 저장 후 리스트 초기화
                }
            }

            // 마지막 남은 데이터를 저장
            if (!entities.isEmpty()) {
                repository.saveAll(entities);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
