package co.instil.interview.pokerface.core;

import static co.instil.interview.pokerface.domain.Hand.HandName.FLUSH;
import static co.instil.interview.pokerface.domain.Hand.HandName.FOUR_OF_A_KIND;
import static co.instil.interview.pokerface.domain.Hand.HandName.FULL_HOUSE;
import static co.instil.interview.pokerface.domain.Hand.HandName.HIGH_CARD;
import static co.instil.interview.pokerface.domain.Hand.HandName.ONE_PAIR;
import static co.instil.interview.pokerface.domain.Hand.HandName.ROYAL_FLUSH;
import static co.instil.interview.pokerface.domain.Hand.HandName.STRAIGHT;
import static co.instil.interview.pokerface.domain.Hand.HandName.STRAIGHT_FLUSH;
import static co.instil.interview.pokerface.domain.Hand.HandName.THREE_OF_A_KIND;
import static co.instil.interview.pokerface.domain.Hand.HandName.TWO_PAIR;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import co.instil.interview.pokerface.domain.Card;
import co.instil.interview.pokerface.domain.Hand;
import co.instil.interview.pokerface.domain.Hand.HandName;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collector;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class HandNameDeterminer {
  private final Map<HandName, Predicate<Hand>> NAME_DETERMINERS_BY_NAME = Map.of(
    FLUSH, this::isFlush,
    FOUR_OF_A_KIND, this::isFourOfAKind,
    FULL_HOUSE, this::isFullHouse,
    ONE_PAIR, this::isOnePair,
    ROYAL_FLUSH, this::isRoyalFlush,
    STRAIGHT, this::isStraight,
    STRAIGHT_FLUSH, this::isStraightFlush,
    THREE_OF_A_KIND, this::isThreeOfAKind,
    TWO_PAIR, this::isTwoPair,
    HIGH_CARD, this::isHighCard
  );

  private static final List<HandName> HAND_NAMES_SORTED_BY_RANK;
  private static final String HIGHEST_RANKS_SEQUENCE;

  static {
    // HAND_NAMES_SORTED_BY_RANK
    @NonNull final HandName[] handNameValues = HandName.values();
    Arrays.sort(handNameValues, Comparator.comparingInt(HandName::getRank).reversed());
    HAND_NAMES_SORTED_BY_RANK = List.of(handNameValues);

    // HIGHEST_RANKS_SEQUENCE
    HIGHEST_RANKS_SEQUENCE = extractCardNamesSequence(Hand.parse("AS KS QS JS TS"));
  }

  public HandName determineName(@NonNull final Hand hand) {
    for (@NonNull final HandName handName : HAND_NAMES_SORTED_BY_RANK) {
      final Predicate<Hand> handPredicate = NAME_DETERMINERS_BY_NAME.get(handName);
      if (handPredicate.test(hand)) {
        return handName;
      }
    }
    throw new IllegalArgumentException(
      "Could not determine the name for hand '" + hand + "'"
    );
  }

  public boolean isHighCard(final Hand hand) {
    return handHasXGroupsOfCardsWithSameName(hand, 5, 1) &&
      !handHasAllCardsWithSequentialNames(hand) &&
      !handHasAllCardsOnSameSuit(hand);
  }

  public boolean isOnePair(@NonNull final Hand hand) {
    return handHasXGroupsOfCardsWithSameName(hand, 4, 2);
  }

  public boolean isTwoPair(@NonNull final Hand hand) {
    return handHasXGroupsOfCardsWithSameName(hand, 3, 2);
  }

  public boolean isThreeOfAKind(@NonNull final Hand hand) {
    return handHasXGroupsOfCardsWithSameName(hand, 3, 3);
  }

  public boolean isStraight(@NonNull final Hand hand) {
    return !handHasAllCardsOnSameSuit(hand) && handHasAllCardsWithSequentialNames(hand);
  }

  public boolean isFlush(@NonNull final Hand hand) {
    // If not all cards belong to the same suit we fail fast
    if (handHasAllCardsOnSameSuit(hand)) {
      final Card[] cards = hand.getCards();
      int differenceByOneOccurrences = 0;
      for (int i = 1; i < cards.length; i++) {
        final Card previousCard = cards[i - 1];
        final Card thisCard = cards[i];
        if (previousCard.getRank() - thisCard.getRank() == 1) {
          ++differenceByOneOccurrences;
        }
      }
      // For a hand to be a flush partial sequences in names are allowed. Although if the entire
      // hand is sequential in name then it really is a straight flush
      return differenceByOneOccurrences < 4;
    }
    return false;
  }

  public boolean isFullHouse(@NonNull final Hand hand) {
    return handHasXGroupsOfCardsWithSameName(hand, 2, 3);
  }

  public boolean isFourOfAKind(@NonNull final Hand hand) {
    return handHasXGroupsOfCardsWithSameName(hand, 2, 4);
  }

  public boolean isStraightFlush(@NonNull final Hand hand) {
    // If not all cards belong to the same suit we fail fast
    return handHasAllCardsOnSameSuit(hand) && handHasAllCardsWithSequentialNames(hand);
  }

  public boolean isRoyalFlush(@NonNull final Hand hand) {
    return hand.getCards()[0].getName() == Card.ACE &&
      handHasAllCardsOnSameSuit(hand) &&
      extractCardNamesSequence(hand).equals(HIGHEST_RANKS_SEQUENCE);
  }

  // Utility methods ///////////////////////////////////////////////////////////////////////////////

  private static String extractCardNamesSequence(@NonNull final Hand hand) {
    return Arrays.stream(hand.getCards())
      .map(Card::getName)
      .collect(Collector.of(
        StringBuilder::new,
        StringBuilder::append,
        StringBuilder::append,
        StringBuilder::toString
      ));
  }

  boolean handHasAllCardsOnSameSuit(@NonNull final Hand hand) {
    return Arrays.stream(hand.getCards())
      .map(Card::getSuit)
      .collect(toSet())
      .size() == 1;
  }

  boolean handHasAllCardsWithSequentialNames(@NonNull final Hand hand) {
    if (!extractCardNamesSequence(hand).equals(HIGHEST_RANKS_SEQUENCE)) {
      final Card[] cards = hand.getCards();
      // Each card has to differ by 1 in rank. The first iteration will point to index 2 and 1
      // which means that we skip Card at 0 completely. We will assess that separately as long as
      // the rest of the N and N - 1 Cards satisfy the condition below
      for (int i = 2; i < cards.length; i++) {
        final Card previousCard = cards[i - 1];
        final Card thisCard = cards[i];
        if (previousCard.getRank() - thisCard.getRank() != 1) {
          return false;
        }
      }
      // Once we've reached this point, it means that 4/5 cards have sequential ranks
      // If the sequence is complete including the first card at index 0 or the hand is (A 5 4 3 2)
      // (which logically really is (5 4 3 2 A)) then we return true
      return cards[0].getRank() - cards[1].getRank() == 1 || (
        cards[0].getName() == Card.ACE && cards[4].getRank() == 2
      );
    }
    return false;
  }

  boolean handHasXGroupsOfCardsWithSameName(
    @NonNull final Hand hand,
    final int x,
    final int highestFrequencyInAnyGroup
  ) {
    assert x > 0 && x <= hand.getCards().length;
    final Map<Character, Long> occurrencesPerName = Arrays.stream(hand.getCards())
      .map(Card::getName)
      .collect(groupingBy(identity(), counting()));
    return occurrencesPerName.size() == x &&
      occurrencesPerName.values().stream().mapToInt(Math::toIntExact)
        .max().orElse(0) == highestFrequencyInAnyGroup;
  }
}
