package com.cast.caspedia.boardgame.dto.bggxmldto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    @JacksonXmlProperty(isAttribute = true)
    private int id;

    private String image;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "name")
    private List<Name> names;

    private String description;

    @JacksonXmlProperty(localName = "yearpublished")
    private Yearpublished  yearpublished ;

    @Data
    public static class Yearpublished {
        @JacksonXmlProperty(isAttribute = true)
        private int value;
    }

    @JacksonXmlProperty(localName = "minplayers")
    private MinPlayers minPlayers;

    @Data
    public static class MinPlayers {
        @JacksonXmlProperty(isAttribute = true)
        private int value;
    }

    @JacksonXmlProperty(localName = "maxplayers")
    private MaxPlayers maxplayers;

    @Data
    public static class MaxPlayers {
        @JacksonXmlProperty(isAttribute = true)
        private int value;
    }

    @JacksonXmlProperty(localName = "minage")
    private Minage minage;

    @Data
    public static class Minage {
        @JacksonXmlProperty(isAttribute = true)
        private int value;
    }

    @JacksonXmlProperty(localName = "minplaytime")
    private Minplaytime minplaytime;

    @Data
    public static class Minplaytime {
        @JacksonXmlProperty(isAttribute = true)
        private int value;
    }

    @JacksonXmlProperty(localName = "maxplaytime")
    private Maxplaytime maxplaytime;

    @Data
    public static class Maxplaytime {
        @JacksonXmlProperty(isAttribute = true)
        private int value;
    }

    @JacksonXmlProperty(localName = "statistics")
    private Statistics statistics;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statistics {
        @JacksonXmlProperty(localName = "ratings")
        private Ratings ratings;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Ratings {
            @JacksonXmlProperty(localName = "average")
            private Average average;

            @JacksonXmlProperty(localName = "averageweight")
            private Averageweight averageweight;

            @Data
            public static class Average {
                @JacksonXmlProperty(isAttribute = true)
                private float value;
            }
            @Data
            public static class Averageweight  {
                @JacksonXmlProperty(isAttribute = true)
                private float value;
            }

        }
    }
}
