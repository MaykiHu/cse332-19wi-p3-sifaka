package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import tests.exceptions.InformativeException;

public class TestingInputs2 {
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
        },
        new Object[]{
            "3r1bnr/p1pk1ppp/n3b3/3p4/1pP3P1/1P3P2/P2PP2P/RN1QKBNR w KQ -",
            new String[][]{{"c4d5"}, {"e2e3"}, {"c4d5"}, {"c4d5"}, {"c4d5"}}
        },
        new Object[]{
            "rn1qkb1r/2Bn1ppp/p7/1ppp4/P7/1N1P2Pb/1PP1PP1P/R2QKBNR b KQkq -",
            new String[][]{{"d8c7"}, {"d8c7"}, {"d8c7"}, {"d8c7"}, {"d8c7"}}
        },
        new Object[]{
            "2b2k2/1rqpn2B/n3p1r1/P1p1P1p1/PN6/3PP2P/1B2K3/3Rb1NR b - -",
            new String[][]{{"a6b4", "e1b4"}, {"a6b4", "e1b4", "b7b4"}, {"e1b4"}, {"b7b4"}, {"e1b4"}}
        },
        new Object[]{
            "1r1bk1nr/3p4/b3p1p1/p1pN1pPp/1n3P2/pPPBP3/P2P4/2R3RK b H -",
            new String[][]{{"b4d3"}, {"b4d3"}, {"b4d3"}, {"b4d3"}, {"b4d3"}}
        },
        new Object[]{
            "3r1b2/pk3p2/3Ppq1n/P1p4r/1p2b1p1/BP1B3P/2QPnP2/R4KR1 w h -",
            new String[][]{{"d3e4"}, {"d3e4"}, {"d3e4"}, {"d3e4"}, {"d3e4"}}
        },
        new Object[]{
            "rn5Q/p2p3b/1b6/1p2k1q1/3p3Q/P1pB1P2/RPP1K2P/2B3NR b - -",
            new String[][]{{"g5f6", "g5g7"}, {"e5d6", "e5e6"}, {"e5d6", "e5e6"}, {"e5d6", "e5e6"}, {"e5d6", "e5e6"}}
        },
        new Object[]{
            "2bk1b1r/2r3p1/2p5/pp1n1p1p/1RB2P1P/4P1N1/P1PPN1K1/2B4R w - -",
            new String[][]{{"c4d5"}, {"c4d5"}, {"b4b5"}, {"b4b5"}, {"c4d5"}}
        },
        new Object[]{
            "1n3k1r/rppb1q1p/3p3n/p1P1bP2/1P1Pp2p/8/P2BP2R/RNQK1BN1 w - -",
            new String[][]{{"d2h6"}, {"d2h6"}, {"d2h6"}, {"d2h6"}, {"d2h6"}}
        },
        new Object[]{
            "1nb1qknr/3p4/2p4P/1pb2p2/1B1P4/QPP1PPp1/r7/R2K3R w - -",
            new String[][]{{"b4c5"}, {"b4c5"}, {"a1a2"}, {"b4c5"}, {"b4c5"}}
        },
        new Object[]{
            "3bk1n1/1q1b1N1r/rpp2ppp/2BP1P2/PpP1P2P/Q2B2P1/3N3R/2R2K2 w - -",
            new String[][]{{"f7d6"}, {"f7d6"}, {"f7d6"}, {"f7d6"}, {"f7d6"}}
        },
        new Object[]{
            "3qk3/r2npp1r/1p1pNn1b/p5pp/1PP3b1/2N1PPPP/PBPKQ3/3R1B1R b k -",
            new String[][]{{"g4e6"}, {"g4e6"}, {"g4e6"}, {"g4e6"}, {"g4e6"}}
        }
    };
}
