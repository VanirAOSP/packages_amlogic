package com.fb.FileBrower;

public class PinyinConv {
    private static int BEGIN = 45217;
    private static int END = 63486;

	private static int[] table = { 45217,45253,45761,46318,46826,47010,47297,
	47614,47614,48119,49062,49324,49896,50371,50614,50622,50906,51387,51446,
	52218,52218,52218,52698,52980,53689,54481, END};

    private static char[] initialtable = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            't', 't', 'w', 'x', 'y', 'z', };

    public static String cn2py(String SourceStr) {
        String Result = "";
        int StrLength = SourceStr.length();
        int i;
        try {
            for (i = 0; i < StrLength; i++) {
                Result += Char2Initial(SourceStr.charAt(i));
            }
        } catch (Exception e) {
            Result = "";
        }
        return Result;
    }

    private static char Char2Initial(char ch) {
        if (ch >= 'a' && ch <= 'z')
        	return ch;
        if (ch >= 'A' && ch <= 'Z')
            return ch;

        int gb = gbValue(ch);

        if ((gb < BEGIN) || (gb > END))
            return ch;

        int i;
        for (i = 0; i < 26; i++) {
                if ((gb >= table[i]) && (gb < table[i+1]))
                    break;
        }
       
        if (gb==END) {
            i=25;
        }
        return initialtable[i];
    }

    private static int gbValue(char ch) {
        String str = new String();
        str += ch;
        try {
            byte[] bytes = str.getBytes("GB2312");
            if (bytes.length < 2)
                return 0;
            return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
        } catch (Exception e) {
            return 0;
        }
    }
}
