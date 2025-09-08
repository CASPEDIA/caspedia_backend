package com.cast.caspedia.boardgame.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "items")
public class BggApiResponseDto {

    @JacksonXmlProperty(localName = "item")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        @JacksonXmlProperty(isAttribute = true)
        private int id;

        @JacksonXmlProperty(localName = "name")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Name> names;

        @JacksonXmlProperty(localName = "image")
        private String imageUrl;

        private ValueElement yearpublished;
        private ValueElement minplayers;
        private ValueElement maxplayers;
        private ValueElement minplaytime;
        private ValueElement maxplaytime;
        private ValueElement minage;

        @JacksonXmlProperty(localName = "description")
        private String description;

        @JacksonXmlProperty(localName = "link")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Link> links;

        private Statistics statistics;
    }

    @Getter
    @Setter
    public static class Name {
        @JacksonXmlProperty(isAttribute = true)
        private String type;
        @JacksonXmlProperty(isAttribute = true)
        private String value;
    }

    @Getter
    @Setter
    public static class Link {
        @JacksonXmlProperty(isAttribute = true)
        private String type;
        @JacksonXmlProperty(isAttribute = true)
        private int id;
        @JacksonXmlProperty(isAttribute = true)
        private String value;
    }

    @Getter
    @Setter
    public static class ValueElement {
        @JacksonXmlProperty(isAttribute = true)
        private int value;
    }

    @Getter
    @Setter
    public static class Statistics {
        private Ratings ratings;
    }


    @Getter
    @Setter
    public static class DecimalValueElement {
        @JacksonXmlProperty(isAttribute = true)
        private float value;
    }

    @Getter
    @Setter
    public static class Ratings {
        private ValueElement usersrated;

        private ValueElement median;
        private ValueElement owned;
        private ValueElement trading;
        private ValueElement wanting;
        private ValueElement wishing;
        private ValueElement numcomments;
        private ValueElement numweights;
        private DecimalValueElement average;       // Geek Score (e.g., "7.58733")
        private DecimalValueElement bayesaverage;
        private DecimalValueElement stddev;
        private DecimalValueElement averageweight; // Geek Weight (e.g., "3.8")
    }
}