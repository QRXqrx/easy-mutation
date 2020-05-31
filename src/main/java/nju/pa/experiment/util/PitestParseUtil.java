package nju.pa.experiment.util;

import nju.pa.experiment.data.mutation.MutantInfo;
import nju.pa.experiment.data.mutation.MutatedFileInfo;
import nju.pa.experiment.data.mutation.MutationResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This class provides static methods which can mutation pitest reports generated
 * by pitest plugin.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-25
 */

public class PitestParseUtil {

    private PitestParseUtil() {}

    final static String UTF8 = "UTF-8";
    final static String INDEX_HTML = "index.html";

    public static MutationResult parse(String summaryReportPath){
        return parse(new File(summaryReportPath));
    }

    /**
     * Parse mutation result start from parsing summary report.
     *
     * @param summaryReport The index.html in the latest folder within pitest folder.
     * @return An instance of MutationResult
     *
     * @see MutationResult
     */
    public static MutationResult parse(File summaryReport){
        MutationResult mutationResult = new MutationResult();

        try {
            Document doc = Jsoup.parse(summaryReport, UTF8);

            /*
                Locate "<tr>" tag like below:

                <tr>
                    <td><a href="./net.mooctest/index.html">net.mooctest</a></td>
                    <td>3</td>
                    <td><div class="coveragePercentage">99% </div><div class="coverage_bar"><div class="coverage_complete" style="width:99%"></div><div class="coverage_ledgend">345/349</div></div></td>
                    <td><div class="coveragePercentage">82% </div><div class="coverage_bar"><div class="coverage_complete" style="width:82%"></div><div class="coverage_ledgend">140/170</div></div></td>
                </tr>
             */
            Element coverageTr = doc.select("table tbody tr").get(0);

            Integer numberOfClasses = Integer.parseInt(coverageTr.select("td") .get(0).ownText());
            String coveragePercent = coverageTr.select("td").get(2).ownText();
            String coverageRatio = coverageTr.select("td div[class=coverage_ledgend]").get(1).ownText();

            // Parse information from class report.
            // Locate report directory.
            File reportDir = summaryReport.getParentFile();
            List<MutatedFileInfo> mutatedFileInfos = generateClassReports(reportDir);

            // Set mutation result.
            mutationResult.setNumberOfClasses(numberOfClasses);
            mutationResult.setCoveragePercentage(coveragePercent);
            mutationResult.setCoverageRatio(coverageRatio);
            mutationResult.setMutatedFileInfos(mutatedFileInfos);
            mutationResult.setReportDir(reportDir.getAbsolutePath());


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Parse failed, in method mutation");
        }

        return mutationResult;
    }

    private static List<MutatedFileInfo> generateClassReports(File reportDir) throws NotDirectoryException {

        List<MutatedFileInfo> mutatedFileInfos = new ArrayList<>();

        List<File> packageDirs = IOUtil.listFilesOrEmpty(reportDir).stream()
                .filter(File::isDirectory).collect(Collectors.toList());

        for (File packageDir : packageDirs) {
            String packageInfo = packageDir.getName();

            // Get all class reports.
            List<File> classReports = IOUtil.listFilesOrEmpty(packageDir)
                    .stream().filter((file) -> !INDEX_HTML.equals(file.getName())).collect(Collectors.toList());

            // Parse class report
            for (File classReport : classReports)
                mutatedFileInfos.add(parseClassReport(classReport, packageInfo));
        }

        return mutatedFileInfos;
    }


    public static MutatedFileInfo parseClassReport(String classReportPath, String packageInfo) {
        return parseClassReport(new File(classReportPath), packageInfo);
    }

    /**
     * Parse a class pitest report to get mutation information for a specific class.
     * The name of the pitest report is like: "RedBlackTree.java.html"
     *
     * @param classReport A File instance of a class pitest report.
     * @return An instance of MutatedFileInfo
     *
     * @see MutatedFileInfo
     */
    public static MutatedFileInfo parseClassReport(File classReport, String packageInfo) {
        MutatedFileInfo mutatedFileInfo = new MutatedFileInfo();

        try {
            Document doc = Jsoup.parse(classReport, UTF8);

            // Locate all mutants
            Element trContainsMutations = doc.select("h2").get(0).parent().parent();
            Element mutantTr = trContainsMutations.nextElementSibling();

            // Collect all <p></p> that contains mutant information.
            int numberOfMutants = 0;
            List<MutantInfo> mutantInfos = new ArrayList<>();
            while(mutantTr != null) {
                // Compute numberOfMutants
                Elements mutantPs = mutantTr.select("p");
                numberOfMutants += mutantPs.size();
                // Update the list of mutantInfos.
                mutantInfos.addAll(parseMutant(mutantTr, doc));

                mutantTr = mutantTr.nextElementSibling(); // Iterator
            }

            // Set mutated class information.
            mutatedFileInfo.setPackageInfo(packageInfo);
            mutatedFileInfo.setFileName(IOUtil.simpleName(classReport));
            mutatedFileInfo.setNumberOfMutants(numberOfMutants);
            mutatedFileInfo.setMutantInfos(mutantInfos);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Parse failed, in method parseClassReport");
        }

        return mutatedFileInfo;
    }


    /**
     * Parse mutant information from "<tr></tr>" tag like below:
     *
     * <tr>
     * <td><a href='#org.pitest.mutationtest.report.html.SourceFile@18539cb1_37'>37</a></td>
     * <td></td>
     * <td>
     * <a name='grouporg.pitest.mutationtest.report.html.SourceFile@18539cb1_37'/>
     * <p class='KILLED'><span class='pop'>1.<span><b>1</b><br/><b>Location : </b>search<br/><b>Killed by : </b>net.mooctest.JPTRedBlackTreeTest.test_1(net.mooctest.JPTRedBlackTreeTest)</span></span>negated conditional &rarr; KILLED</p>
     * <p class='KILLED'><span class='pop'>2.<span><b>2</b><br/><b>Location : </b>search<br/><b>Killed by : </b>net.mooctest.JPTRedBlackTreeTest.testBTree_4(net.mooctest.JPTRedBlackTreeTest)</span></span> negated conditional &rarr; KILLED</p>
     * <p class='KILLED'><span class='pop'>3.<span><b>3</b><br/><b>Location : </b>search<br/><b>Killed by : </b>net.mooctest.JPTRedBlackTreeTest.testBTree_4(net.mooctest.JPTRedBlackTreeTest)</span></span> negated conditional &rarr; KILLED</p>
     * </td>
     * </tr>
     *
     * Each "<p></p>" tag contains information about a mutant.
     *
     * @param mutantTr An Element instance indicates <tr></tr> tag contains mutants information.
     * @param doc A Document instance gotten from passing a pitest report.
     * @return A list of MutantInfo contained in mutantTr.
     *
     * @see MutantInfo
     */
    public static List<MutantInfo> parseMutant(Element mutantTr, Document doc) {
        List<MutantInfo> mutantInfos = new ArrayList<>();

        // Get lineNumber
        Integer lineNumber = Integer.parseInt(mutantTr.select("td a").get(0).ownText());

        // Get sourceCode
        String sourceCode = getSourceCode(lineNumber, doc);

        // Generate mutantInfo for each mutant.
        Elements killedPs = mutantTr.select("p[class=KILLED]");
        Elements survivedPs = mutantTr.select("p[class=SURVIVED]");
        Elements timedOutPs = mutantTr.select("p[class=TIMED_OUT]");
        for (Element p : killedPs)
            mutantInfos.add(generateMutantInfo(lineNumber, sourceCode, "KILLED", p));
        for (Element p : survivedPs)
            mutantInfos.add(generateMutantInfo(lineNumber, sourceCode, "SURVIVED", p));
        for (Element p : timedOutPs)
            mutantInfos.add(generateMutantInfo(lineNumber, sourceCode, "TIMED_OUT", p));

        return mutantInfos;
    }

    private static String getSourceCode(int lineNumber, Document doc) {
        String sourceCodeSelector = "a[href$=_" + lineNumber + "]";
        Element aTag = doc.select(sourceCodeSelector).get(0);
        Element sourceCodeTd = aTag.parent().parent().nextElementSibling();
        Element sourceCodeSpan = sourceCodeTd.select("span").get(0);
        return sourceCodeSpan.ownText().trim();
    }

    private static MutantInfo generateMutantInfo(
            Integer lineNumber,
            String sourceCode,
            String status,
            Element mutantP
    ) {
        // "&rarr;" == "→"
        String description = mutantP.ownText().substring(0, (mutantP.ownText().indexOf("→") - 1)).trim();
        String killedBy = mutantP.select("span span").get(0).ownText().trim();
        return new MutantInfo( lineNumber, sourceCode, status, description, killedBy );
    }

}
