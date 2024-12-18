# Runs Java Lambda in Many Threads

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/together)](http://www.rultor.com/p/yegor256/together)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/together/actions/workflows/mvn.yml/badge.svg)](https://github.com/yegor256/together/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/together)](http://www.0pdd.com/p?name=yegor256/together)
[![Maven Central](https://img.shields.io/maven-central/v/com.yegor256/together.svg)](https://maven-badges.herokuapp.com/maven-central/com.yegor256/together)
[![Javadoc](http://www.javadoc.io/badge/com.yegor256/together.svg)](http://www.javadoc.io/doc/com.yegor256/together)
[![codecov](https://codecov.io/gh/yegor256/together/branch/master/graph/badge.svg)](https://codecov.io/gh/yegor256/together)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/together)](https://hitsofcode.com/view/github/yegor256/together)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/together/blob/master/LICENSE.txt)

With this small Java library you can test your objects
for thread-safety by doing some manipulations with them
in multiple parallel threads. You may read this blog post,
in order to understand the motivation for this type of
testing better: [How I Test My Java Classes for Thread-Safety][blog].

By the way, there are similar libraries for Java, but they are
either more complex or less tests-oriented, for example
[ConcurrentUnit](https://github.com/jhalterman/concurrentunit)
or
`Threads` class in [Cactoos](https://github.com/yegor256/cactoos).

First, you add this library to your `pom.xml`:

```xml
<dependency>
  <groupId>com.yegor256</groupId>
  <artifactId>together</artifactId>
  <version>0.0.1</version>
</dependency>
```

Then, you use it like this, in your JUnit5 test:

```java
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.yegor256.Together;

class FooTest {
  @Test
  void worksAsExpected() {
    MatcherAssert.assertThat(
      "processes all lambdas successfully",
      new Together<>(
        () -> {
          // do the job
          return true;
        }
      ),
      Matchers.not(Matchers.hasItem(Matchers.is(false)))
    );
  }
}
```

Here, the "job" will be executed in multiple threads, in parallel,
and the result of each execution will be collected in a list. Then,
the list will be checked for any `false` values. If there is at least
one, the test will fail.

## How to Contribute

Fork repository, make changes, send us a
[pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 11+.

[blog]: https://www.yegor256.com/2018/03/27/how-to-test-thread-safety.html
