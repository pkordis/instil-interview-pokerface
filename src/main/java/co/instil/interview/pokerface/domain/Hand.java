package co.instil.interview.pokerface.domain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
public class Hand {
  private final Card[] cards;

  public static Hand parse(@NonNull final String handString) {
    final String[] cardStrings = handString.split("\\s+");
    Throwable cause = null;
    if (cardStrings.length == 5) {
      try {
        final Set<Card> theseCards = new HashSet<>();
        for (int i = 0; i < cardStrings.length; i++) {
          theseCards.add(Card.parse(cardStrings[i]));
        }
        if (theseCards.size() != 5) {
          throw new IllegalStateException("One or more Cards where found identical");
        }
        final Card[] cards = theseCards.toArray(new Card[5]);
        Arrays.sort(cards, Comparator.reverseOrder());
        return Hand.builder()
          .cards(cards)
          .build();
      } catch (final Throwable thisCause) {
        cause = thisCause;
      }
    }
    throw new IllegalArgumentException(
      "The input string [" + handString + "] is not a valid Hand", cause
    );
  }

  public String toString() {
    return String.join(" ", Arrays.stream(cards).map(Card::toString).toArray(String[]::new));
  }

  @Getter
  @RequiredArgsConstructor
  public enum HandName {
    FLUSH("Flush", 6),
    FOUR_OF_A_KIND("Four of a kind", 8),
    FULL_HOUSE("Full house", 7),
    HIGH_CARD("High card", 1),
    ONE_PAIR("One pair", 2),
    ROYAL_FLUSH("Royal Flush", 10),
    STRAIGHT("Straight", 5),
    STRAIGHT_FLUSH("Straight flush", 9),
    THREE_OF_A_KIND("Three of a kind", 4),
    TWO_PAIR("Two pair", 3);

    private final String label;

    /**
     * Represents the significance of the hand's name. The greater the value of the integer
     * representation, the more value the hand's name has. This
     */
    private final int rank;

    @Override
    public String toString() {
      return label;
    }
  }
}
