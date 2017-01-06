import java.util.HashSet;
import java.util.Set;

public class PalindromePairs {
    public static class Pair {
        public Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }
        public int i, j;
    }

    private static boolean isPalindrome(String s, int b, int e) {
        return e <= b || s.charAt(b) == s.charAt(e) && isPalindrome(s, b + 1, e - 1);
    }

    // O(n^2k) where k is a limit on the length of the words
    public static Set<Pair> findPalindromePairsSlow(String... words) {
        Set<Pair> result = new HashSet<>();

        for (int i = 0; i < words.length; ++i) {
            for (int j = 0; j < words.length; ++j) {
                if (i == j)
                    continue;
                if (isPalindrome(words[i] + words[j], 0, words[i].length() + words[j].length() - 1))
                    result.add(new Pair(i, j));
            }
        }

        return result;
    }


    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];     // We assume only small English letters
        Set<Integer> wordIndices = new HashSet<>(); // Only used when it is the end (beginning) of (a) word(s).
    }

    // We calculate the length of all palindromes in input string. We start by adding # signs between all letters,
    // at the beginning of the string, and at the end of teh string. We do that so all palindromes will be of odd size.
    // So result[2x] is the length of the palindrome centered at gap between indices x - 1 and x in s
    // for 0 <= x <= s.length(). Therefore always result[0] == result[2 * s.length()] == 0.
    // result[2x + 1] is the length of the palindrome centered at index x in s for 0 <= x <= s.length() - 1
    private static int[] findAllPalindromes(String s) {
        if (s.length() == 0)
            return null;

        if (s.length() == 1)
            return new int[]{0, 1, 0};

        int[] w = new int[2 * s.length() + 1]; // Maintain the wing lengths
        w[1] = 1;
        int max = 1;                           // Index of the center of maximum palindrome
        int leader = 1;                        // Index of the center of the leader

        int e = 3;
        for (int c = 2; c < w.length; ++c) {
            int buddy = leader - (c - leader);
            if (buddy - w[buddy] > leader - w[leader]) {
                w[c] = Math.min(w[buddy], (w.length - 1) - c);
                continue;   // We spend only O(1) on this center. Thank you Mancher.
            }

            int b = c - (e - c);
            while (b >= 0 && e < w.length) {
                if (e % 2 == 0 ^ b % 2 == 0 || e % 2 != 0 && b % 2 != 0 && s.charAt(b / 2) != s.charAt(e / 2))
                    break;
                --b;
                ++e;
            }

            w[c] = e - c - 1;
            if (w[c] > w[max])
                max = c;
            leader = c;
        }

        return w;
    }

    // O(nk) where k is a limit on the length of the words
    public static Set<Pair> findPalindromePairsFast(String... words) {
        Set<Pair> result = new HashSet<>();

        // Build trie of words in O(nk).
        TrieNode rootOfWordsTrie = new TrieNode();
        for (int i = 0; i < words.length; ++i) {
            TrieNode node = rootOfWordsTrie;
            for (int j = 0; j < words[i].length(); ++j) {
                char c = words[i].charAt(j);
                TrieNode child = node.children[c - 'a'];
                if (child != null)
                    node = child;
                else
                    node = node.children[c - 'a'] = new TrieNode();
            }
            node.wordIndices.add(i);
        }

        // Build trie of inverse of words in O(nk).
        TrieNode rootOfInversedWordsTrie = new TrieNode();
        for (int i = 0; i < words.length; ++i) {
            TrieNode node = rootOfInversedWordsTrie;
            for (int j = words[i].length() - 1; j >= 0 ; --j) {
                char c = words[i].charAt(j);
                TrieNode child = node.children[c - 'a'];
                if (child != null)
                    node = child;
                else
                    node = node.children[c - 'a'] = new TrieNode();
            }
            node.wordIndices.add(i);
        }

        for (int i = 0; i < words.length; ++i) {        // A loop of n iterations
            String word = words[i];
            int[] palindromes = findAllPalindromes(word); // We use "Mancher" so O(k)

            TrieNode node = rootOfWordsTrie;
            for (int k = word.length() - 1; k >= 0; --k) {   // A loop of k iterations. From the end to the beginning
                char c = word.charAt(k);
                if (node.children[c - 'a'] == null)
                    break;

                node = node.children[c - 'a'];

                assert palindromes != null;
                boolean isPalindromeLongEnough = palindromes[k] / 2 >= k / 2;

                if (node.wordIndices.isEmpty() || !isPalindromeLongEnough)
                    continue;

                for (int j : node.wordIndices)
                    if (i != j)
                        result.add(new Pair(j, i));      // words[i] == P + inverse(W), words[j] == W. P might be empty.
            }

            node = rootOfInversedWordsTrie;
            for (int k = 0; k < word.length(); ++k) {   // A loop of k iterations. From the beginning to the end.
                char c = word.charAt(k);
                if (node.children[c - 'a'] == null)
                    break;

                node = node.children[c - 'a'];

                int matchedCharacters = k + 1;
                int leftOverCharacters = word.length() - matchedCharacters;
                int indexInPalindromesArray = 2 * (matchedCharacters + leftOverCharacters / 2) + leftOverCharacters % 2 == 0 ? 0 : 1;
                assert palindromes != null;
                boolean isPalindromeLongEnough = palindromes[indexInPalindromesArray] / 2 >= leftOverCharacters / 2;

                if (node.wordIndices.isEmpty() || !isPalindromeLongEnough)
                    continue;

                for (int j : node.wordIndices)
                    if (i != j && words[i].length() != words[j].length())
                        result.add(new Pair(i, j));      // words[i] == W + P, words[j] == inverse(W). P can't be empty.
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Set<Pair> result = findPalindromePairsSlow("bat", "tab", "cat");
        for (Pair p : result)
            System.out.print("[" + p.i + ", " + p.j + "] ");    // [[0, 1], [1, 0]]
        System.out.println();
        result = findPalindromePairsFast("bat", "tab", "cat");
        for (Pair p : result)
            System.out.print("[" + p.i + ", " + p.j + "] ");    // [[0, 1], [1, 0]]
        System.out.println();

        result = findPalindromePairsSlow("abcd", "dcba", "lls", "s", "sssll");
        for (Pair p : result)
            System.out.print("[" + p.i + ", " + p.j + "] ");  // [[0, 1], [1, 0], [3, 2], [2, 4]]
        System.out.println();
        result = findPalindromePairsFast("abcd", "dcba", "lls", "s", "sssll");
        for (Pair p : result)
            System.out.print("[" + p.i + ", " + p.j + "] ");  // [[0, 1], [1, 0], [3, 2], [2, 4]]
        System.out.println();

        result = findPalindromePairsSlow("abcdc", "ba", "ba");
        for (Pair p : result)
            System.out.print("[" + p.i + ", " + p.j + "] ");  // [[0, 1], [1, 0], [3, 2], [2, 4]]
        System.out.println();
        result = findPalindromePairsSlow("abcdc", "ba", "ba");
        for (Pair p : result)
            System.out.print("[" + p.i + ", " + p.j + "] ");  // [[0, 1], [1, 0], [3, 2], [2, 4]]
        System.out.println();

        result = findPalindromePairsSlow("abcdccdc", "ba", "cdcba");
        for (Pair p : result)
            System.out.print("[" + p.i + ", " + p.j + "] ");  // [[0, 1], [1, 0], [3, 2], [2, 4]]
        System.out.println();
        result = findPalindromePairsSlow("abcdccdc", "ba", "cdcba");
        for (Pair p : result)
            System.out.print("[" + p.i + ", " + p.j + "] ");  // [[0, 1], [1, 0], [3, 2], [2, 4]]
        System.out.println();
    }
}