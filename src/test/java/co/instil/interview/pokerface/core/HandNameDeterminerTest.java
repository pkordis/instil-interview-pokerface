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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import co.instil.interview.pokerface.domain.Hand;
import co.instil.interview.pokerface.domain.Hand.HandName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({
  MockitoExtension.class
})
class HandNameDeterminerTest {

  @Spy
  private HandNameDeterminer determiner;

  @Test
  void determineNameShouldCallAllMatchersByNameRankUntilTheIsHighCardEventuallyReturnsTrue() {
    // given
    final Hand hand = Hand.parse("AH 6D 8S 5C TH");

    // when
    final HandName handName = determiner.determineName(hand);

    // then
    assertThat(handName).isEqualTo(HIGH_CARD);
    verify(determiner, times(1)).isFlush(same(hand));
    verify(determiner, times(1)).isFourOfAKind(same(hand));
    verify(determiner, times(1)).isFullHouse(same(hand));
    verify(determiner, times(1)).isOnePair(same(hand));
    verify(determiner, times(1)).isRoyalFlush(same(hand));
    verify(determiner, times(1)).isStraight(same(hand));
    verify(determiner, times(1)).isStraightFlush(same(hand));
    verify(determiner, times(1)).isThreeOfAKind(same(hand));
    verify(determiner, times(1)).isTwoPair(same(hand));
    verify(determiner, times(1)).isHighCard(same(hand));
  }

  @Test
  void determineNameShouldReturnTheRightHandNameForTheRightInputHand() {
    assertThat(determiner.determineName(Hand.parse("AH AD KS JC TH"))).isEqualTo(ONE_PAIR);
    assertThat(determiner.determineName(Hand.parse("AH AD KS KC TH"))).isEqualTo(TWO_PAIR);
    assertThat(determiner.determineName(Hand.parse("AH AD AS KC TH"))).isEqualTo(THREE_OF_A_KIND);
    assertThat(determiner.determineName(Hand.parse("TS 9D 8S 7H 6C"))).isEqualTo(STRAIGHT);
    assertThat(determiner.determineName(Hand.parse("2D TD QD AD KD"))).isEqualTo(FLUSH);
    assertThat(determiner.determineName(Hand.parse("AH AD AS TC TH"))).isEqualTo(FULL_HOUSE);
    assertThat(determiner.determineName(Hand.parse("9H 9D 3S 9S 9C"))).isEqualTo(FOUR_OF_A_KIND);
    assertThat(determiner.determineName(Hand.parse("KH QH JH TH 9H"))).isEqualTo(STRAIGHT_FLUSH);
    assertThat(determiner.determineName(Hand.parse("AH KH QH JH TH"))).isEqualTo(ROYAL_FLUSH);

  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH KH QH JH TH",
    "AD KD QD JD TD",
    "AS KS QS JS TS",
    "AC KC QC JC TC"
  })
  void isRoyalFlushShouldReturnTrueIfHandIsRoyalFlush(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isRoyalFlush = determiner.isRoyalFlush(thisHand);

    // then
    assertThat(isRoyalFlush).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH KH QH JH 9H",
    "AD KD QD JD TS",
    "3H JS 3C 7C 5D"
  })
  void isRoyalFlushShouldReturnFalseIfHandIsNotRoyalFlush(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isRoyalFlush = determiner.isRoyalFlush(thisHand);

    // then
    assertThat(isRoyalFlush).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "KH QH JH TH 9H",
    "JD TD QD 9D 8D",
    "5S 4S 3S 2S AS",
    "TC 9C 8C 7C 6C"
  })
  void isStraightFlushShouldReturnTrueIfHandIsStraightFlush(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isStraightFlush = determiner.isStraightFlush(thisHand);

    // then
    assertThat(isStraightFlush).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH KH QH JH 9H",
    "JD 9D QD AD KD",
    "KS 5S 4S 3S 2S",
    "TC 9C 8C 7C 5C",
    "AH KH QH JH TS",
    "AH KH QH JH TH" // Royal Flush
  })
  void isStraightFlushShouldReturnFalseIfHandIsNotStraightFlush(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isStraightFlush = determiner.isStraightFlush(thisHand);

    // then
    assertThat(isStraightFlush).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "KH QS JD TC 9H",
    "JS TD QH 9D 8D",
    "5S 4D 3S 2S AS",
    "TS 9D 8S 7H 6C"
  })
  void isStraightShouldReturnTrueIfHandIsStraight(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isStraight = determiner.isStraight(thisHand);

    // then
    assertThat(isStraight).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH KH QH JH 9H",
    "JD 9D QD AD KD",
    "KS 5S 4S 3S 2S",
    "TC 9C 8C 7C 5C",
    "AH KH QH JH TS",
    "AH KH QH JH TH" // Royal Flush
  })
  void isStraightShouldReturnFalseIfHandIsNotStraight(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isStraight = determiner.isStraight(thisHand);

    // then
    assertThat(isStraight).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD AS AC TH",
    "9D 8H 8D 8S 8C",
    "5S 5H 4D 5D 5C",
    "TC 2H 2D 2S 2C"
  })
  void isFourOfAKindShouldReturnTrueIfHandIsFourOfAKind(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isFourOfAKind = determiner.isFourOfAKind(thisHand);

    // then
    assertThat(isFourOfAKind).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD AS KC TH",
    "9D 8H 8D 8S 7C"
  })
  void isFourOfAKindShouldReturnFalseIfHandIsNotFourOfAKind(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isFourOfAKind = determiner.isFourOfAKind(thisHand);

    // then
    assertThat(isFourOfAKind).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD AS TC TH",
    "9D 9H 8D 8S 8C",
    "5S 5H 4S 5D 4C",
    "TC TH 2D 2S 2C"
  })
  void isFullHouseShouldReturnTrueIfHandIsFullHouse(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isFullHouse = determiner.isFullHouse(thisHand);

    // then
    assertThat(isFullHouse).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD AS KC TH",
    "9D 9H 8D 8S 7C"
  })
  void isFullHouseReturnFalseIfHandIsNotFullHouse(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isFullHouse = determiner.isFullHouse(thisHand);

    // then
    assertThat(isFullHouse).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH KH QH JH 6H",
    "2D TD QD AD KD",
    "5S 4S 8S 2S 9S",
    "TC 8C 6C 4C 2C"
  })
  void isFlushShouldReturnTrueIfHandIsFlush(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isFlush = determiner.isFlush(thisHand);

    // then
    assertThat(isFlush).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "KH QH JH TH 9H", // Straight Flush
    "AH KH QH JH TH"  // Royal Flush
  })
  void isFlushShouldReturnFalseIfHandIsNotFlush(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isFlush = determiner.isFlush(thisHand);

    // then
    assertThat(isFlush).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD AS KC TH",
    "3H 4D 5S 5C 5H"
  })
  void isThreeOfAKindShouldReturnTrueIfHandIsThreeOfAKind(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isThreeOfAKind = determiner.isThreeOfAKind(thisHand);

    // then
    assertThat(isThreeOfAKind).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "9H 9D 3S 9S 9C", // Four Of A Kind
    "KH QH JH TH 9H", // Straight Flush
    "AH KH QH JH TH"  // Royal Flush
  })
  void isThreeOfAKindShouldReturnFalseIfHandIsNotThreeOfAKind(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isThreeOfAKind = determiner.isThreeOfAKind(thisHand);

    // then
    assertThat(isThreeOfAKind).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD KS KC TH",
    "3H 3D AS 5C 5H"
  })
  void isTwoPairShouldReturnTrueIfHandIsTwoPair(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isTwoPair = determiner.isTwoPair(thisHand);

    // then
    assertThat(isTwoPair).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD AS KC TH", // Three Of A Kind
    "9H 9D 3S 9S 9C", // Four Of A Kind
    "KH QH JH TH 9H", // Straight Flush
    "AH KH QH JH TH"  // Royal Flush
  })
  void isTwoPairShouldReturnFalseIfHandIsNotTwoPair(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isTwoPair = determiner.isTwoPair(thisHand);

    // then
    assertThat(isTwoPair).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD KS JC TH",
    "TH 3D AS 5C 5H"
  })
  void isOnePairShouldReturnTrueIfHandIsOnePair(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isOnePair = determiner.isOnePair(thisHand);

    // then
    assertThat(isOnePair).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD KS KC TH", // Two Pair
    "AH AD AS KC TH", // Three Of A Kind
    "9H 9D 3S 9S 9C", // Four Of A Kind
    "KH QH JH TH 9H", // Straight Flush
    "AH KH QH JH TH"  // Royal Flush
  })
  void isOnePairShouldReturnFalseIfHandIsNotOnePair(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isOnePair = determiner.isOnePair(thisHand);

    // then
    assertThat(isOnePair).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH 2D QS JC TH",
    "AH 6D 8S 5C TH"
  })
  void isHighCardShouldReturnTrueIfHandIsHighCard(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isHighCard = determiner.isHighCard(thisHand);

    // then
    assertThat(isHighCard).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AH AD KS JC TH", // One Pair
    "AH AD KS KC TH", // Two Pair
    "AH AD AS KC TH", // Three Of A Kind
    "TS 9D 8S 7H 6C", // Straight
    "2D TD QD AD KD", // Flush
    "9D 9H 8D 8S 8C", // Full House
    "9H 9D 3S 9S 9C", // Four Of A Kind
    "KH QH JH TH 9H", // Straight Flush
    "AH KH QH JH TH"  // Royal Flush
  })
  void isHighCardShouldReturnFalseIfHandIsNotHighCard(final String handString) {
    // given
    final Hand thisHand = Hand.parse(handString);

    // when
    final boolean isHighCard = determiner.isHighCard(thisHand);

    // then
    assertThat(isHighCard).isFalse();
  }

  @Test
  void isHighCardShouldSpecificBehaviour() {
    // given
    final Hand hand = Hand.parse("AH 2S 3D 8C TH");
    final HandNameDeterminer spiedDeterminer = spy(determiner);

    // when
    final boolean isHighCard = spiedDeterminer.isHighCard(hand);

    // then
    assertThat(isHighCard).isTrue();
    verify(spiedDeterminer, times(1)).handHasXGroupsOfCardsWithSameName(hand, 5, 1);
    verify(spiedDeterminer, times(1)).handHasAllCardsWithSequentialNames(hand);
    verify(spiedDeterminer, times(1)).handHasAllCardsOnSameSuit(hand);
  }
}