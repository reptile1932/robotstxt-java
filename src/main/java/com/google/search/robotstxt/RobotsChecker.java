package com.google.search.robotstxt;

import com.google.common.collect.Lists;

import java.util.Objects;

public class RobotsChecker {

    private final byte[] robotsTxtContents;
    private final Parser parser;
    private final RobotsMatcher matcher;

    public RobotsChecker(byte[] robotsTxtContents) {
        Objects.requireNonNull(robotsTxtContents, "robotsTxtContents cannot be null");
        this.robotsTxtContents = robotsTxtContents;
        parser = new RobotsParser(new RobotsParseHandler());
        matcher = (RobotsMatcher) parser.parse(robotsTxtContents);
    }

    /**
     * 某个 ua 是否可以访问某个 url
     * @param userAgent
     * @param url
     * @return
     */
    public boolean canFetch(String userAgent, String url) {
        return matcher.allowedByRobots(Lists.newArrayList(userAgent), url);
    }

}
