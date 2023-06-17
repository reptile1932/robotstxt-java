//// Copyright 2020 Google LLC
////
//// Licensed under the Apache License, Version 2.0 (the "License");
//// you may not use this file except in compliance with the License.
//// You may obtain a copy of the License at
////
////      http://www.apache.org/licenses/LICENSE-2.0
////
//// Unless required by applicable law or agreed to in writing, software
//// distributed under the License is distributed on an "AS IS" BASIS,
//// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//// See the License for the specific language governing permissions and
//// limitations under the License.
//
//package com.google.search.robotstxt;
//
//import static com.google.common.truth.Truth.assertThat;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.Collections;
//import org.junit.Test;
//
///**
// * Unit tests validating parsing behavior.
// *
// * @see RobotsParser
// */
//public class RobotsParserTest {
//  /**
//   * Parses given robots.txt contents via {@link RobotsParser} and compares the result with an
//   * expected one.
//   *
//   * @param robotsTxtBody Contents of robots.txt file
//   * @param expectedContents Expected contents
//   */
//  private static void parseAndValidate(
//      final String robotsTxtBody, final RobotsContents expectedContents) {
//    final Parser parser = new RobotsParser(new RobotsParseHandler());
//    final Matcher matcher = parser.parse(robotsTxtBody.getBytes(StandardCharsets.UTF_8));
//    final RobotsContents actualContents = ((RobotsMatcher) matcher).getRobotsContents();
//
//    expectedContents
//        .getGroups()
//        .forEach(expectedGroup -> assertThat(expectedGroup).isIn(actualContents.getGroups()));
//  }
//
//  /** Verifies: rules grouping, rules parsing, invalid directives ignorance. */
//  @Test
//  public void testMultipleGroups() {
//    final String robotsTxtBody =
//        "allow: /foo/bar/\n"
//            + "\n"
//            + "user-agent: FooBot\n"
//            + "disallow: /\n"
//            + "allow: /x/\n"
//            + "user-agent: BarBot\n"
//            + "disallow: /\n"
//            + "allow: /y/\n"
//            + "\n"
//            + "\n"
//            + "allow: /w/\n"
//            + "user-agent: BazBot\n"
//            + "\n"
//            + "user-agent: FooBot\n"
//            + "allow: /z/\n"
//            + "disallow: /\n";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Arrays.asList(
//                new RobotsContents.Group(
//                    Collections.singletonList("FooBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/x/"))),
//                new RobotsContents.Group(
//                    Collections.singletonList("BarBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/y/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/w/"))),
//                new RobotsContents.Group(
//                    Arrays.asList("BazBot", "FooBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/z/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/")))));
//
//    parseAndValidate(robotsTxtBody, expectedContents);
//  }
//
//  /** Verifies: CR character must be treated as EOL, invalid directives ignorance. */
//  @Test
//  public void testCrParsing() {
//    final String robotsTxtBody =
//        "user-agent: FooBot\n"
//            + "disallow: /\n"
//            + "allow: /x/\rallow: /y/\n"
//            + "al\r\r\r\r\rdisallow: /z/\n";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Collections.singletonList(
//                new RobotsContents.Group(
//                    Collections.singletonList("FooBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/x/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/y/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/z/")))));
//
//    parseAndValidate(robotsTxtBody, expectedContents);
//  }
//
//  /** Verifies: CL RF must be treated as EOL. */
//  @Test
//  public void testCrLfParsing() {
//    final String robotsTxtBody =
//        "allow: /foo/bar/\r\n"
//            + "\r\n"
//            + "user-agent: FooBot\r\n"
//            + "disallow: /\r\n"
//            + "allow: /x/\r\n"
//            + "user-agent: BarBot\r\n"
//            + "disallow: /\r\n"
//            + "allow: /y/\r\n"
//            + "\r\n";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Arrays.asList(
//                new RobotsContents.Group(
//                    Collections.singletonList("FooBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/x/"))),
//                new RobotsContents.Group(
//                    Collections.singletonList("BarBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/y/")))));
//
//    parseAndValidate(robotsTxtBody, expectedContents);
//  }
//
//  /** Verifies: Last line may not end with EOL. */
//  @Test
//  public void testNoFinalNewline() {
//    final String robotsTxtBody =
//        "User-Agent: foo\n"
//            + "Allow: /some/path\n"
//            + "User-Agent: bar\n"
//            + "\n"
//            + "\n"
//            + "Disallow: /";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Arrays.asList(
//                new RobotsContents.Group(
//                    Collections.singletonList("foo"),
//                    Collections.singletonList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/some/path"))),
//                new RobotsContents.Group(
//                    Collections.singletonList("bar"),
//                    Collections.singletonList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/")))));
//
//    parseAndValidate(robotsTxtBody, expectedContents);
//  }
//
//  /** Verifies: surrounding whitespace characters (LF, CR) ignorance. */
//  @Test
//  public void testWhitespacesParsing() {
//    final String robotsTxtBody =
//        "user-agent \t: \tFooBot\n"
//            + "disallow  : /  \n"
//            + "  allow:  /x/\n"
//            + "    \n"
//            + " \t \t \n"
//            + "user-agent:BarBot\n"
//            + "\t \t disallow\t \t :\t \t /\t \t \n"
//            + "\t\tallow\t\t:\t\t/y/\t\t\n"
//            + "\n";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Arrays.asList(
//                new RobotsContents.Group(
//                    Collections.singletonList("FooBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/x/"))),
//                new RobotsContents.Group(
//                    Collections.singletonList("BarBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "/y/")))));
//
//    parseAndValidate(robotsTxtBody, expectedContents);
//  }
//
//  /** Verifies: global rules parsing. */
//  @Test
//  public void testGlobalGroup() {
//    final String robotsTxtBody =
//        "User-agent: *\n" + "Disallow: /x/\n" + "User-agent: FooBot\n" + "Disallow: /y/\n";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Arrays.asList(
//                new RobotsContents.Group(
//                    Collections.emptyList(),
//                    Collections.singletonList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/x/")),
//                    true),
//                new RobotsContents.Group(
//                    Collections.singletonList("FooBot"),
//                    Collections.singletonList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/y/")))));
//
//    parseAndValidate(robotsTxtBody, expectedContents);
//  }
//
//  /** [Google-specific] Verifies: assuming colon if it's missing. */
//  @Test
//  public void testMissingSeparator() {
//    final String robotsTxtBody = "user-agent FooBot\n" + "disallow /\n" + "allow foo bar\n";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Collections.singletonList(
//                new RobotsContents.Group(
//                    Collections.singletonList("FooBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/"),
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.ALLOW, "foo bar")))));
//
//    parseAndValidate(robotsTxtBody, expectedContents);
//  }
//
//  /** [Google-specific] Verifies: trimming values to specific number of bytes. */
//  @Test
//  public void testTrimmingToBytes() {
//    final String robotsTxtBody = "user-agent: FooBot\n" + "disallow: /foo/bar/baz/qux\n";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Collections.singletonList(
//                new RobotsContents.Group(
//                    Collections.singletonList("FooBot"),
//                    Collections.singletonList(
//                        new RobotsContents.Group.Rule(Parser.DirectiveType.DISALLOW, "/foo/b")))));
//
//    final Parser parser = new RobotsParser(new RobotsParseHandler(), 8);
//    final Matcher matcher = parser.parse(robotsTxtBody.getBytes(StandardCharsets.UTF_8));
//    final RobotsContents actualContents = ((RobotsMatcher) matcher).getRobotsContents();
//
//    expectedContents
//        .getGroups()
//        .forEach(expectedGroup -> assertThat(expectedGroup).isIn(actualContents.getGroups()));
//  }
//
//  /** Verifies: Path normalisation corner case. */
//  @Test
//  public void testPathNormalisationCornerCase() {
//    final String robotsTxtBody =
//        "user-agent: FooBot\n" + "disallow: /foo?bar%aa%\n" + "disallow: /foo?bar%aa%a\n";
//
//    final RobotsContents expectedContents =
//        new RobotsContents(
//            Collections.singletonList(
//                new RobotsContents.Group(
//                    Collections.singletonList("FooBot"),
//                    Arrays.asList(
//                        new RobotsContents.Group.Rule(
//                            Parser.DirectiveType.DISALLOW, "/foo?bar%AA%"),
//                        new RobotsContents.Group.Rule(
//                            Parser.DirectiveType.DISALLOW, "/foo?bar%AA%a")))));
//
//    parseAndValidate(robotsTxtBody, expectedContents);
//  }
//}
