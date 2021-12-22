/*
 * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.thekrechetofficial.util.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Jpeg;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.pdf.PdfGState;
import java.awt.Color;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import ru.thekrechetofficial.entity.Category;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.Pilot;
import ru.thekrechetofficial.entity.Vehicle;
import ru.thekrechetofficial.util.Data;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
public class PdfCreator {

    public static void createBigTable() {

        try ( Document document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10)) {
            // step2
            PdfWriter.getInstance(document,
                    new FileOutputStream("AddBigTable.pdf"));
            // step3
            document.open();
            // step4
            String[] bogusData = {"M0065920", "SL", "FR86000P", "PCGOLD",
                "119000", "96 06", "2001-08-13", "4350", "6011648299",
                "FLFLMTGP", "153", "119000.00"};
            int NumColumns = 12;

            PdfPTable datatable = new PdfPTable(NumColumns);
            int headerwidths[] = {9, 4, 8, 10, 8, 11, 9, 7, 9, 10, 4, 10}; // percentage
            datatable.setWidths(headerwidths);
            datatable.setWidthPercentage(100); // percentage
            datatable.getDefaultCell().setPadding(3);
            datatable.getDefaultCell().setBorderWidth(2);
            datatable.getDefaultCell().setHorizontalAlignment(
                    Element.ALIGN_CENTER);
            datatable.addCell("Clock #");
            datatable.addCell("Trans Type");
            datatable.addCell("Cusip");
            datatable.addCell("Long Name");
            datatable.addCell("Quantity");
            datatable.addCell("Fraction Price");
            datatable.addCell("Settle Date");
            datatable.addCell("Portfolio");
            datatable.addCell("ADP Number");
            datatable.addCell("Account ID");
            datatable.addCell("Reg Rep ID");
            datatable.addCell("Amt To Go ");

            datatable.setHeaderRows(1); // this is the end of the table header

            datatable.getDefaultCell().setBorderWidth(1);
            for (int i = 1; i < 750; i++) {
                if (i % 2 == 1) {
                    datatable.getDefaultCell().setGrayFill(0.9f);
                }
                for (int x = 0; x < NumColumns; x++) {
                    datatable.addCell(bogusData[x]);
                }
                if (i % 2 == 1) {
                    datatable.getDefaultCell().setGrayFill(1);
                }
            }
            document.add(datatable);
            document.close();
        } catch (Exception de) {
            de.printStackTrace();
        }
        // step5

    }

    public static void createList() {
        // step 1: creation of a document-object

        try ( Document document = new Document();) {
            // step 2:
            PdfWriter.getInstance(document, new FileOutputStream("lists.pdf"));
            //HtmlWriter.getInstance(document, new FileOutputStream("lists.html"));

            // step 3: we open the document
            document.open();

            // step 4:
            com.lowagie.text.List list = new com.lowagie.text.List(true, 20);
            list.add(new ListItem("First line"));
            list.add(new ListItem("The second line is longer to see what happens once the end of the line is reached. Will it start on a new line?"));
            list.add(new ListItem("Third line"));
            document.add(list);

            document.add(new Paragraph("some books I really like:"));
            ListItem listItem;
            list = new com.lowagie.text.List(true, 15);
            listItem = new ListItem("When Harlie was one", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
            listItem.add(new Chunk(" by David Gerrold", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)));
            list.add(listItem);
            listItem = new ListItem("The World according to Garp", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
            listItem.add(new Chunk(" by John Irving", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)));
            list.add(listItem);
            listItem = new ListItem("Decamerone", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
            listItem.add(new Chunk(" by Giovanni Boccaccio", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)));
            list.add(listItem);
            document.add(list);

            Paragraph paragraph = new Paragraph("some movies I really like:");
            list = new com.lowagie.text.List(false, 10);
            list.add("Wild At Heart");
            list.add("Casablanca");
            list.add("When Harry met Sally");
            list.add("True Romance");
            list.add("Le mari de la coiffeuse");
            paragraph.add(list);
            document.add(paragraph);

            document.add(new Paragraph("Some authors I really like:"));
            list = new com.lowagie.text.List(false, 20);
            list.setListSymbol(new Chunk("\u2022", FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD)));
            listItem = new ListItem("Isaac Asimov");
            list.add(listItem);
            com.lowagie.text.List sublist;
            sublist = new com.lowagie.text.List(false, true, 10);
            sublist.setListSymbol(new Chunk("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
            sublist.add("The Foundation Trilogy");
            sublist.add("The Complete Robot");
            sublist.add("Caves of Steel");
            sublist.add("The Naked Sun");
            list.add(sublist);
            listItem = new ListItem("John Irving");
            list.add(listItem);
            sublist = new com.lowagie.text.List(false, true, 10);
            sublist.setFirst('a');
            sublist.setListSymbol(new Chunk("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
            sublist.add("The World according to Garp");
            sublist.add("Hotel New Hampshire");
            sublist.add("A prayer for Owen Meany");
            sublist.add("Widow for a year");
            list.add(sublist);
            listItem = new ListItem("Kurt Vonnegut");
            list.add(listItem);
            sublist = new com.lowagie.text.List(false, true, 10);
            sublist.setListSymbol(new Chunk("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
            sublist.add("Slaughterhouse 5");
            sublist.add("Welcome to the Monkey House");
            sublist.add("The great pianola");
            sublist.add("Galapagos");
            list.add(sublist);
            document.add(list);
            document.close();
        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        // step 5: we close the document
    }

    public static void createParagraph() {

        try ( Document document = new Document()) {
            // step 2:
            // we create a writer that listens to the document
            PdfWriter.getInstance(document, new FileOutputStream("Paragraphs.pdf"));

            // step 3: we open the document
            document.open();
            // step 4:
            Paragraph p1 = new Paragraph(new Chunk(
                    "This is my first paragraph. ",
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            p1.add("The leading of this paragraph is calculated automagically. ");
            p1.add("The default leading is 1.5 times the fontsize. ");
            p1.add(new Chunk("You can add chunks "));
            p1.add(new Phrase("or you can add phrases. "));
            p1.add(new Phrase(
                    "Unless you change the leading with the method setLeading, the leading doesn't change if you add text with another leading. This can lead to some problems.",
                    FontFactory.getFont(FontFactory.HELVETICA, 18)));
            document.add(p1);
            Paragraph p2 = new Paragraph(new Phrase(
                    "This is my second paragraph. ", FontFactory.getFont(
                            FontFactory.HELVETICA, 12)));
            p2.add("As you can see, it started on a new line.");
            document.add(p2);
            Paragraph p3 = new Paragraph("This is my third paragraph.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12));
            document.add(p3);

        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        // step 5: we close the document
    }

    public static void createPdf(java.util.List<Lap> laps) {

        try ( Document document = new Document()) {
            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("HelloWorld.pdf"));

            // step 3: we open the document
            document.open();
            //writer.getInfo().put(PdfName.CREATOR, new PdfString(Document.getVersion()));
            // step 4: we add a paragraph to the document
            document.add(new Paragraph("Hello World"));
            //Image jpg = Image.getInstance("C:\\progLife\\src\\projects\\rally-sprint-events\\src\\main\\resources\\top.png");
            Image jpg = Image.getInstance("src\\main\\resources\\top.png");
            document.add(jpg);

            document.close();
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }

    }

    public static void createAbsoluteReport(List<Lap> laps, boolean penalty) {
        try ( Document document = new Document();) {
            PdfWriter.getInstance(document, new FileOutputStream("AbsoluteList.pdf"));

            document.open();

            com.lowagie.text.List list = new com.lowagie.text.List(true, 20);

            for (Lap l : laps) {
                Crew c = l.getCrew();
                Vehicle v = c.getVehicle();
                Pilot p = c.getPilot();

                String item = String.format("%d %s %s %d %s",
                        c.getStartNumber(),
                        p.getLastName() + " " + p.getFirstName(),
                        v.getMake() + " " + v.getModel(),
                        v.getHp(),
                        l.getFormattedTotalLapTime()
                );

                list.add(new ListItem(item));
            }

            document.add(list);

            document.close();
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }

    }

    public static void createStagePilotsTable(HashMap<String, List<Lap>> lapMap, String name, boolean withPenalty) {

        if (!lapMap.isEmpty()) {

            try ( Document document = new Document(PageSize.A4, 10, 10, 10, 10)) {

                String fileName = name.replaceAll("\\s", "_");
                PdfWriter.getInstance(document,
                        new FileOutputStream("Pilot_" + fileName + "_Table.pdf"));

                document.open();

                Font boldFont = new Font(Font.HELVETICA, Font.DEFAULTSIZE, Font.BOLD);

                Paragraph top = new Paragraph("Rally Sprint Events");
                top.setAlignment(Element.ALIGN_RIGHT);

                Paragraph titleParagraph = new Paragraph();
                titleParagraph.setAlignment(Element.ALIGN_CENTER);
                titleParagraph.setSpacingBefore(15.f);
                Phrase titlePhrase = new Phrase(name + ": ПОКРУГОВКА");
                titleParagraph.add(titlePhrase);

                Paragraph tableParagraph = new Paragraph();
                tableParagraph.setSpacingBefore(0.7f);

                document.add(top);
                document.add(titleParagraph);

                int numColumns = 5;
                int padding = 3;
                int borderWidth = 1;

                PdfPTable datatable = new PdfPTable(numColumns);
                int headerwidths[] = {20, 20, 20, 20, 20}; // percentage
                datatable.setWidths(headerwidths);
                datatable.setWidthPercentage(100); // percentage
                datatable.getDefaultCell().setPadding(padding);
                datatable.getDefaultCell().setBorderWidth(borderWidth);
                datatable.getDefaultCell().setHorizontalAlignment(
                        Element.ALIGN_CENTER);
                datatable.getDefaultCell().setVerticalAlignment(
                        Element.ALIGN_MIDDLE);

                for (Map.Entry<String, List<Lap>> e : lapMap.entrySet()) {

                    String crew = e.getKey();
                    List<Lap> laps = e.getValue();

                    if (!laps.isEmpty()) {
                        PdfPCell crewDataCell = new PdfPCell(new Phrase(crew, boldFont));
                        crewDataCell.setColspan(numColumns);
                        crewDataCell.setMinimumHeight(25f);
                        crewDataCell.setGrayFill(0.9f);
                        crewDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        crewDataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        datatable.addCell(crewDataCell);
                        int iter = 0;
                        int row = 0;
                        int multy = 0;
                        int rows = (laps.size() % numColumns == 0) ? (laps.size() / numColumns) : (laps.size() / numColumns + 1);

                        for (int i = 0; i < rows * numColumns; i++) {
                            iter++;

                            int lapIndex = row + rows * multy;
                            multy++;

                            if (lapIndex < laps.size()) {
                                String num = String.format("%02d: ", lapIndex + 1);
                                Chunk lap = new Chunk(laps.get(lapIndex).getFormattedTotalLapTime());
                                Chunk numChunk = new Chunk(num, boldFont);
                                Paragraph lapInfo = new Paragraph();
                                lapInfo.add(numChunk);
                                lapInfo.add(lap);
                                PdfPCell cell = new PdfPCell(lapInfo);
                                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                cell.setPadding(padding);
                                cell.setBorderWidth(0);
                                cell.setPaddingLeft(5);
                                cell.setPaddingBottom(6);
                                cell.setBorderColorLeft(Color.WHITE);
                                cell.setBorderColorRight(Color.WHITE);
                                datatable.addCell(cell);
                            } else {
                                PdfPCell cell = new PdfPCell(new Phrase(""));
                                cell.setBorderWidth(0);
                                datatable.addCell(cell);
                            }

                            if (iter % 5 == 0) {
                                row++;
                                multy = 0;
                            }

                        }

                        PdfPCell emptyCell = new PdfPCell();
                        emptyCell.setColspan(numColumns);
                        emptyCell.setMinimumHeight(30.f);
                        emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        emptyCell.setBorderWidthBottom(0);
                        emptyCell.setBorderWidthLeft(0);
                        emptyCell.setBorderWidthRight(0);
                        //emptyCell.setBorderWidth(0);
                        datatable.addCell(emptyCell);

                    }
                }
                tableParagraph.add(datatable);
                document.add(tableParagraph);

                document.close();
            } catch (Exception de) {
                de.printStackTrace();
            }

        }

    }

    public static void createStageAbsoluteTable(List<Lap> laps, String name) {

        if (!laps.isEmpty()) {

            try ( Document document = new Document(PageSize.A4, 10, 10, 10, 10)) {

                String fileName = name.replaceAll("\\s", "_");
                PdfWriter writer = PdfWriter.getInstance(document,
                        new FileOutputStream("Absolute_" + fileName + "_Table.pdf"));

                document.open();

                //background image
//                //C:\\progLife\\src\\projects\\rally-sprint-events\\src\\main\\webapp\\resources\\png\\set.png
//                Image label = Image.getInstance("C:\\progLife\\src\\projects\\rally-sprint-events\\src\\main\\webapp\\resources\\png\\set.png");
//                PdfContentByte canvas = writer.getDirectContentUnder();
//                label.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
//                label.setAlignment(Image.UNDERLYING);
//                label.setAbsolutePosition(0, 0);
//                PdfContentByte cbu = writer.getDirectContentUnder();
//                //cbu.setRGBColorFill(0x1E, 0x1E, 0x1E); //0x00, 0x00, 0x00
//                
//                cbu.rectangle(0.0f, 0.0f, document.getPageSize().getWidth(), document.getPageSize().getHeight());
//                cbu.fill();
//                cbu.sanityCheck();
//                canvas.saveState();
//                PdfGState state = new PdfGState();
//                state.setFillOpacity(0.15f);
//                canvas.setGState(state);
//                canvas.addImage(label);
//                canvas.restoreState();
                Font boldFont = new Font(Font.HELVETICA, Font.DEFAULTSIZE, Font.BOLD);

                Paragraph top = new Paragraph("Rally Sprint Events");
                top.setAlignment(Element.ALIGN_RIGHT);

                Paragraph titleParagraph = new Paragraph();
                titleParagraph.setAlignment(Element.ALIGN_CENTER);
                titleParagraph.setSpacingBefore(15.f);
                Phrase titlePhrase = new Phrase(name + ": АБСОЛЮТНЫЙ ЗАЧЕТ");
                titleParagraph.add(titlePhrase);

                document.add(top);
                document.add(titleParagraph);

                Paragraph tableParagraph = new Paragraph();
                tableParagraph.setSpacingBefore(0.7f);

                int NumColumns = 7;
                int padding = 3;
                int borderWidth = 1;

                PdfPTable datatable = new PdfPTable(NumColumns);
                int headerwidths[] = {6, 6, 25, 27, 6, 15, 15}; // percentage
                datatable.setWidths(headerwidths);
                datatable.setWidthPercentage(100); // percentage
                datatable.getDefaultCell().setPadding(padding);
                datatable.getDefaultCell().setBorderWidth(borderWidth);
                datatable.getDefaultCell().setHorizontalAlignment(
                        Element.ALIGN_CENTER);
                datatable.getDefaultCell().setVerticalAlignment(
                        Element.ALIGN_MIDDLE);
                datatable.addCell("");
                datatable.addCell(new Phrase("#", boldFont));
                datatable.addCell(new Phrase("PILOT", boldFont));
                datatable.addCell(new Phrase("VEHICLE", boldFont));
                datatable.addCell(new Phrase("HP", boldFont));
                datatable.addCell(new Phrase("TIME", boldFont));
                datatable.addCell(new Phrase("GAP", boldFont));
                datatable.setHeaderRows(1); // this is the end of the table header

                datatable.getDefaultCell().setBorderWidth(1);

                for (int i = 1; i <= laps.size(); i++) {

                    if (i % 2 == 1) {
                        datatable.getDefaultCell().setGrayFill(0.9f);
                    }

                    Lap l = laps.get(i - 1);
                    Crew c = l.getCrew();
                    Vehicle v = c.getVehicle();
                    Pilot p = c.getPilot();

                    long delta = l.getLapTime() + l.getPenalty().getPenaltyTime() - laps.get(0).getLapTime();
                    Duration total = Duration.ofMillis(delta);
                    String formattedDelta = String.format("%02d:%02d.%03d",
                            total.toMinutesPart(),
                            total.toSecondsPart(),
                            total.toMillisPart());

                    String[] rowData = {String.valueOf(i),
                        String.valueOf(c.getStartNumber()),
                        p.getLastName() + " " + p.getFirstName(),
                        v.getMake() + " " + v.getModel() + " (" + v.getYear() + " г.)",
                        String.valueOf(v.getHp()),
                        l.getFormattedTotalLapTime(),
                        delta != 0 ? ("+" + formattedDelta) : "---"
                    };
                    for (int x = 0; x < NumColumns; x++) {

                        PdfPCell cell = new PdfPCell(new Phrase(rowData[x]));
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setPadding(padding);
                        cell.setBorderWidth(borderWidth);
                        cell.setPaddingLeft(5);
                        cell.setPaddingBottom(6);

                        if (x == 2 || x == 3) {
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        } else {
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        }

                        if (i % 2 == 1) {
                            cell.setGrayFill(0.9f);
                        }

                        datatable.addCell(cell);

                    }

                }

                tableParagraph.add(datatable);
                document.add(tableParagraph);

                document.close();
            } catch (Exception de) {
                de.printStackTrace();
            }

        }

    }

    public static void createStageCatBestResultTable(HashMap<Category, Set<Lap>> catMap, String name) {

        if (!catMap.isEmpty()) {

            try ( Document document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10)) {

                String fileName = name.replaceAll("\\s", "_");
                PdfWriter.getInstance(document,
                        new FileOutputStream("Cat_" + fileName + "_Table.pdf"));

                document.open();

                Font boldFont = new Font(Font.HELVETICA, Font.DEFAULTSIZE, Font.BOLD);

                Paragraph top = new Paragraph("Rally Sprint Events");
                top.setAlignment(Element.ALIGN_RIGHT);

                Paragraph titleParagraph = new Paragraph();
                titleParagraph.setAlignment(Element.ALIGN_CENTER);
                titleParagraph.setSpacingBefore(15.f);
                Phrase titlePhrase = new Phrase(name + ": ЛУЧШЕЕ ВРЕМЯ ПО КЛАССАМ");
                titleParagraph.add(titlePhrase);

                Paragraph tableParagraph = new Paragraph();
                tableParagraph.setSpacingBefore(0.7f);

                document.add(top);
                document.add(titleParagraph);

                int numColumns = 10;
                int padding = 3;
                int borderWidth = 1;

                PdfPTable datatable = new PdfPTable(numColumns);
                int headerwidths[] = {4, 5, 19, 19, 4, 9, 9, 13, 9, 9}; // percentage
                datatable.setWidths(headerwidths);
                datatable.setWidthPercentage(100); // percentage
                datatable.getDefaultCell().setPadding(padding);
                datatable.getDefaultCell().setBorderWidth(borderWidth);
                datatable.getDefaultCell().setHorizontalAlignment(
                        Element.ALIGN_CENTER);
                datatable.getDefaultCell().setVerticalAlignment(
                        Element.ALIGN_MIDDLE);
                datatable.addCell(new Phrase("P", boldFont));
                datatable.addCell(new Phrase("#", boldFont));
                datatable.addCell(new Phrase("PILOT", boldFont));
                datatable.addCell(new Phrase("VEHICLE", boldFont));
                datatable.addCell(new Phrase("HP", boldFont));
                datatable.addCell(new Phrase("LAPTIME", boldFont));
                datatable.addCell(new Phrase("PENALTY", boldFont));
                datatable.addCell(new Phrase("DESCRIPTION", boldFont));
                datatable.addCell(new Phrase("TOTAL", boldFont));
                datatable.addCell(new Phrase("GAP", boldFont));
                datatable.setHeaderRows(1); // this is the end of the table header

                datatable.getDefaultCell().setBorderWidth(1);

                for (Map.Entry<Category, Set<Lap>> e : catMap.entrySet()) {

                    Category cat = e.getKey();
                    Set<Lap> laps = e.getValue();
                    PdfPCell catNameCell = new PdfPCell(new Phrase(cat.getName(), boldFont));
                    catNameCell.setColspan(numColumns);
                    catNameCell.setMinimumHeight(30.f);
                    catNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    catNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    datatable.addCell(catNameCell);

                    int iter = 0;
                    long best = 0;
                    for (Lap l : laps) {
                        iter++;
                        Crew c = l.getCrew();
                        Vehicle v = c.getVehicle();
                        Pilot p = c.getPilot();

                        if (best == 0) {
                            best = l.getLapTime() + l.getPenalty().getPenaltyTime();
                        }

                        long delta = l.getLapTime() + l.getPenalty().getPenaltyTime() - best;
                        Duration total = Duration.ofMillis(delta);
                        String formattedDelta = String.format("%02d:%02d.%03d",
                                total.toMinutesPart(),
                                total.toSecondsPart(),
                                total.toMillisPart());

                        String[] rowData = {String.valueOf(iter),
                            "[" + String.valueOf(c.getStartNumber()) + "]",
                            p.getLastName() + " " + p.getFirstName(),
                            v.getMake() + " " + v.getModel() + " (" + v.getYear() + " г.)",
                            String.valueOf(v.getHp()),
                            l.getFormattedTotalLapTime(),
                            l.getPenalty().getFormattedPenaltyTime(),
                            l.getPenalty().getDescription(),
                            l.getFormattedTotalLapTime(),
                            delta != 0 ? ("+" + formattedDelta) : "---"
                        };

                        for (int x = 0; x < numColumns; x++) {

                            PdfPCell cell = new PdfPCell(new Phrase(rowData[x]));
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            cell.setPadding(padding);
                            cell.setBorderWidth(borderWidth);
                            cell.setPaddingLeft(5);
                            cell.setPaddingBottom(6);

                            if (x == 2 || x == 3) {
                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            } else {
                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            }

                            if (iter % 2 == 1) {
                                cell.setGrayFill(0.9f);
                            }

                            datatable.addCell(cell);
                        }
                    }

                }
                tableParagraph.add(datatable);
                document.add(tableParagraph);

                document.close();

            } catch (Exception de) {
                de.printStackTrace();
            }
        }

    }

    public static void createFinalCatResultTable(HashMap<Category, Set<List<Lap>>> catMap, String name, int su) {

        if (!catMap.isEmpty()) {

            try ( Document document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10)) {

                String fileName = name.replaceAll("\\s", "_");
                PdfWriter.getInstance(document,
                        new FileOutputStream("Final_" + fileName + "_Table.pdf"));

                document.open();

                Font boldFont = new Font(Font.HELVETICA, Font.DEFAULTSIZE, Font.BOLD);

                Paragraph top = new Paragraph("Rally Sprint Events");
                top.setAlignment(Element.ALIGN_RIGHT);

                Paragraph titleParagraph = new Paragraph();
                titleParagraph.setAlignment(Element.ALIGN_CENTER);
                titleParagraph.setSpacingBefore(15.f);
                Phrase titlePhrase = new Phrase(name + ": ЛУЧШЕЕ ВРЕМЯ ПО КЛАССАМ");
                titleParagraph.add(titlePhrase);

                Paragraph tableParagraph = new Paragraph();
                tableParagraph.setSpacingBefore(0.7f);

                document.add(top);
                document.add(titleParagraph);

                int numColumns = 6 + su;
                int padding = 3;
                int borderWidth = 1;

                PdfPTable datatable = new PdfPTable(numColumns);
                int headerwidths[] = new int[numColumns];//{4, 5, 19, 19, 4, 9, 9, 13, 9, 9}; // percentage
                headerwidths[0] = 4;
                headerwidths[1] = 5;
                int width = (100 - 9 * (su + 1)) / 2;
                headerwidths[2] = width;
                headerwidths[3] = width;
                for (int i = 4; i < headerwidths.length; i++) {
                    headerwidths[i] = 9;
                }

                datatable.setWidths(headerwidths);
                datatable.setWidthPercentage(100); // percentage
                datatable.getDefaultCell().setPadding(padding);
                datatable.getDefaultCell().setBorderWidth(borderWidth);
                datatable.getDefaultCell().setHorizontalAlignment(
                        Element.ALIGN_CENTER);
                datatable.getDefaultCell().setVerticalAlignment(
                        Element.ALIGN_MIDDLE);
                datatable.addCell(new Phrase("P", boldFont));
                datatable.addCell(new Phrase("#", boldFont));
                datatable.addCell(new Phrase("PILOT", boldFont));
                datatable.addCell(new Phrase("VEHICLE", boldFont));
                for (int i = 1; i <= su; i++) {
                    datatable.addCell(new Phrase("SU-" + i, boldFont));
                }
                datatable.addCell(new Phrase("TOTAL", boldFont));
                datatable.addCell(new Phrase("GAP", boldFont));
                datatable.setHeaderRows(1); // this is the end of the table header

                datatable.getDefaultCell().setBorderWidth(1);

                for (Map.Entry<Category, Set<List<Lap>>> e : catMap.entrySet()) {

                    Category cat = e.getKey();
                    Set<List<Lap>> lapListSet = e.getValue();

                    PdfPCell catNameCell = getCell(new Phrase(cat.getName(), boldFont), numColumns);
                    datatable.addCell(catNameCell);

                    int iter = 0;
                    long best = 0;
                    for (List<Lap> list : lapListSet) {
                        if (!list.isEmpty()) {
                            iter++;

                            PdfPCell cell = getCell(new Phrase(String.valueOf(iter)));
                            datatable.addCell(cell);

                            PdfPCell sNCell = getCell(new Phrase(String.valueOf(list.get(0).getCrew().getStartNumber())));
                            datatable.addCell(sNCell);

                            PdfPCell pilotCell = getCell(new Phrase(list.get(0).getCrew().getPilot().getLastName() + " " + list.get(0).getCrew().getPilot().getFirstName()));
                            pilotCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            datatable.addCell(pilotCell);
                            
                            PdfPCell vehicleCell = getCell(new Phrase(list.get(0).getCrew().getVehicle().getMake() + " " + list.get(0).getCrew().getVehicle().getModel()));
                            vehicleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            datatable.addCell(vehicleCell);

                            long sum = 0;
                            long delta = 0;
                            for (int li = 0; li < su; li++) {
                                Lap lap = list.get(li);
                                sum += lap.getLapTime() + lap.getPenalty().getPenaltyTime();

                                PdfPCell lapCell = getCell(new Phrase(lap.getFormattedTotalLapTime()));
                                datatable.addCell(lapCell);
                            }

                            if (best == 0) {
                                best = sum;
                            }

                            Duration total = Duration.ofMillis(sum);
                            String formattedDelta = String.format("%02d:%02d.%03d",
                                    total.toMinutesPart(),
                                    total.toSecondsPart(),
                                    total.toMillisPart());
                            PdfPCell sumCell = getCell(new Phrase(String.valueOf(formattedDelta)));
                            datatable.addCell(sumCell);

                            delta = sum - best;
                            String text = delta == 0 ? "---" : String.format("%02d:%02d.%03d",
                                    Duration.ofMillis(delta).toMinutesPart(),
                                    Duration.ofMillis(delta).toSecondsPart(),
                                    Duration.ofMillis(delta).toMillisPart());
                            PdfPCell gapCell = getCell(new Phrase(text));
                            datatable.addCell(gapCell);
                        }

                    }

                }

                tableParagraph.add(datatable);
                document.add(tableParagraph);

                document.close();

            } catch (Exception de) {
                de.printStackTrace();
            }
        }

    }

    private static PdfPCell getCell(Phrase phrase, int numColumns) {

        PdfPCell cell = new PdfPCell(new Phrase(phrase));
        cell.setColspan(numColumns);
        cell.setMinimumHeight(30.f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return cell;

    }

    private static PdfPCell getCell(Phrase phrase) {

        PdfPCell cell = new PdfPCell(new Phrase(phrase));
        cell.setMinimumHeight(30.f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return cell;

    }

}
