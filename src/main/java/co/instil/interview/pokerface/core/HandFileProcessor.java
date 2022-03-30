package co.instil.interview.pokerface.core;

import co.instil.interview.pokerface.domain.Hand;
import co.instil.interview.pokerface.domain.Hand.HandName;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandFileProcessor {
  @NonNull
  private final HandNameDeterminer handNameDeterminer;

  public void processFile(final Path path) {
    try (final Stream<String> lines = Files.lines(path, Charset.defaultCharset())) {
      final AtomicLong lineNumber = new AtomicLong(0L);
      lines.forEachOrdered(line -> {
        final long thisLineNumber = lineNumber.incrementAndGet();
        try {
          final Hand hand = Hand.parse(line);
          final HandName handName = handNameDeterminer.determineName(hand);
          System.out.printf("%s => %s%n", hand, handName);
        } catch (final IllegalArgumentException iae) {
          System.out.printf("line: %d - ERROR: %s%n", thisLineNumber, iae.getMessage());
        }
      });
    } catch (final Throwable e) {
      System.out.println(
        "Failed to process input file. Make sure its path is valid, it's readable of text format\n"
          + "and is not shared from a network location"
      );
    }
  }
}
