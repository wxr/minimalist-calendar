import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

public class MinimalistCalendar {

    private static final int YEAR_START = 2018;
    private static final int YEAR_END   = 2028;

    private static final String DELIMITER = "   ";

    private static final PDFont DEFAULT_FONT       = PDType1Font.HELVETICA;
    private static final float  DEFAULT_FONT_SIZE  = 12;
    private static final Color  DEFAULT_COLOR      = Color.GRAY;

    private static final PDFont EMPHASIS_FONT      = PDType1Font.HELVETICA_BOLD;
    private static final float  EMPHASIS_FONT_SIZE = 12;
    private static final Color  EMPHASIS_COLOR     = Color.BLACK;

    public static void main(String[] args) throws Exception {
        for (int year = YEAR_START; year <= YEAR_END; ++year) {
            generate(year);
        }
    }

    private static void generate(final int year) throws Exception {
        PDDocument document = new PDDocument();

        PDRectangle rectangle = new PDRectangle(PDRectangle.LETTER.getHeight(), PDRectangle.LETTER.getWidth());
        PDPage page = new PDPage(rectangle);

        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(DEFAULT_FONT, DEFAULT_FONT_SIZE);
        contentStream.setNonStrokingColor(DEFAULT_COLOR);

        // title
        contentStream.newLineAtOffset(70, 550);
        for (int i = 0; i <= 3; ++i) {
            if (i > 0) contentStream.showText(DELIMITER);
            contentStream.showText(String.valueOf(year).substring(i, i + 1));
        }

        // body
        for (int month = 1; month <= 12; ++month) {
            YearMonth yearMonth = YearMonth.of(year, month);
            for (int day = 1; day <= 31; ++day) {
                if (!yearMonth.isValidDay(day)) break;

                if (day == 1)
                    contentStream.newLineAtOffset(0, -40);
                else
                    contentStream.showText(DELIMITER);

                LocalDate localDate = LocalDate.of(year, month, day);
                if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    contentStream.setFont(EMPHASIS_FONT, EMPHASIS_FONT_SIZE);
                    contentStream.setNonStrokingColor(EMPHASIS_COLOR);
                    contentStream.showText(String.valueOf(day));
                    contentStream.setFont(DEFAULT_FONT, DEFAULT_FONT_SIZE);
                    contentStream.setNonStrokingColor(DEFAULT_COLOR);
                } else {
                    contentStream.showText(String.valueOf(day));
                }
            }
        }

        contentStream.endText();
        contentStream.close();
        document.save("./pdf/" + year + ".pdf");
        document.close();
    }

}
