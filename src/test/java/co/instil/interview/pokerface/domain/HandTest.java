package co.instil.interview.pokerface.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HandTest {
  @ParameterizedTest
  @ValueSource(strings = {
    "3H JS 3C 7C 5D",
    "3H           JS           3C  7C 5D",
    "9H  9D  3S  9S  9C"
  })
  void parseShouldParseTheInputStringToAHand(final String handString) {
    // when
    final Hand hand = Hand.parse(handString);

    // then
    assertThat(hand).isNotNull();
    assertThat(hand.getCards()).isNotNull();
    assertThat(hand.getCards().length).isEqualTo(5);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "3H JS JS JS 3H",
    "3H JS 7C 7C 5D"
  })
  void parseShouldFailIfOneOrMoreCardsInTheHandAreIdentical(final String handString) {
    // when
    final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
      () -> Hand.parse(handString));

    // then
    assertThat(e.getCause()).isNotNull();
    assertThat(e.getCause().getMessage()).isEqualTo("One or more Cards where found identical");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "3H JD JC JS 3H AH",
    "3H JS 8C 7C",
    "3H JS 8C",
    "AH"
  })
  void parseShouldFailIfTheHandDoesNotConsistOfFiveCards(final String handString) {
    // when
    final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
      () -> Hand.parse(handString));

    // then
    assertThat(e.getCause()).isNull();
    assertThat(e.getMessage())
      .isEqualTo("The input string [" + handString + "] is not a valid Hand");
  }
}
