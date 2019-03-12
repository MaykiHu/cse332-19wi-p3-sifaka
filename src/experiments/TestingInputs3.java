package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import tests.exceptions.InformativeException;

public class TestingInputs3 {
    public static Object[][] FENS_TO_TEST = new Object[][] {
        new Object[]{
            "r1bq1b1r/pppkpppp/3p4/8/8/P2PP2P/1PP2PP1/RNB1KBNR b KQ -",
            new String[][]{{"e7e5"}, {"d7e8", "d7c6"}, {"e7e6"}, {"e7e6"}, {"c7c5"}}
        },
        new Object[]{
            "rnbqk1n1/1pppb1p1/p6r/2N1PpBp/4P3/1P6/P1P1KPPP/R2Q1BNR b kq -",
            new String[][]{{"e7g5"}, {"e7g5"}, {"e7g5"}, {"e7g5"}, {"e7g5"}}
        },
        new Object[]{
            "r1bqkbr1/1ppppp1N/p1n3pp/8/1P2PP2/3P4/P1P2nPP/RNBQKBR1 b KQkq -",
            new String[][]{{"f2d1"}, {"f2d1"}, {"f2d1"}, {"f2d1"}, {"f2d1"}}
        },
        new Object[]{
            "rnbqkbr1/pp1p1ppp/2p1p3/1N1n4/P3P3/5N1P/1PPPQPP1/R1B1KBR1 b KQ -",
            new String[][]{{"c6b5"}, {"d5f4"}, {"d5f4"}, {"d5b4"}, {"d5f6"}}
        },
        new Object[]{
            "rnbqkbnr/1pp2pp1/p2pp2p/8/2P1P2P/3P1PP1/PP5R/RNBQKBN1 b KQkq -",
            new String[][]{{"h6h5"}, {"b8c6", "g8f6"}, {"c7c6"}, {"b8c6", "g8f6"}, {"b8c6", "g8f6", "c8d7", "f8e7", "e6e5"}}
        },
        new Object[]{
            "r1bqk1n1/ppppnpp1/B4r2/4p2p/P2bP3/6NP/1PPPQPP1/RNB2KR1 b kq -",
            new String[][]{{"b7a6"}, {"f6f2"}, {"f6f2"}, {"f6f2"}, {"f6f2"}}
        },
        new Object[]{
            "r1bqkbnr/ppp1ppp1/5n2/3p3p/8/1PP1PNP1/P2P1PBP/RNBQ1RK1 b Hkq -",
            new String[][]{{"c8f5"}, {"c8g4"}, {"c8g4"}, {"c8g4"}, {"c8f5"}}
        },
        new Object[]{
            "r1bqkb1r/ppppppp1/7p/8/1n4PP/2P2P2/PP1BP3/R2QKBNR b KQkq h3",
            new String[][]{{"b4c6", "b4d5"}, {"b4d5"}, {"b4c6"}, {"b4c6"}, {"b4d5"}}
        },
        new Object[]{
            "rnbqkb2/3ppppr/1P3n1p/p7/6P1/7N/PBPPPP1P/RN1QKBR1 b Qk -",
            new String[][]{{"e7e5"}, {"d8b6"}, {"d8b6"}, {"d8b6"}, {"d8b6"}}
        },
        new Object[]{
            "1nbqkbnr/rp3ppp/3p4/p1p5/P2NP3/3K4/1PPP1PPP/1RBQ1B1R b kq -",
            new String[][]{{"c5d4"}, {"c5d4"}, {"c5d4"}, {"c5d4"}, {"c5d4"}}
        },
        new Object[]{
            "rnbqkb1r/p2p1p2/2p1p2p/1p1N2p1/2P1n3/P3PP2/RP1P2PP/1NBQKB1R b KQkq -",
            new String[][]{{"c6d5"}, {"c6d5", "b5c4"}, {"c6d5"}, {"c6d5"}, {"c6d5"}}
        }
    };
}
