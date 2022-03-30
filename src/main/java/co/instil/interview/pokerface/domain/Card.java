package co.instil.interview.pokerface.domain;

import java.util.regex.Pattern;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
@EqualsAndHashCode
public class Card implements Comparable<Card> {
  public static final char ACE = 'A';
  public static final String NAMES = "23456789TJQK" + ACE;
  static final Pattern CARD_PATTERN = Pattern.compile(
    "[" + NAMES + "][HDSC]"
  );

  private final char name;
  private final char suit;
  private final int rank;

  public static Card parse(@NonNull final String cardString) {
    if (cardString.length() == 2) {
      // Avoid normalize loads of data if input string is not 2 characters exactly
      final String normalizedCardString = cardString.toUpperCase();
      if (CARD_PATTERN.matcher(normalizedCardString).matches()) {
        return Card.builder()
          .name(normalizedCardString.charAt(0))
          .rank(NAMES.indexOf(normalizedCardString.charAt(0)) + 2)
          .suit(normalizedCardString.charAt(1))
          .build();
      }
    }
    throw new IllegalArgumentException("The input string [" + cardString + "] is not a valid Card");
  }

  @Override
  public int compareTo(final Card otherCard) {
    return Integer.compare(this.getRank(), otherCard.getRank());
  }

  @Override
  public String toString() {
    return name + "" + suit;
  }
}
