package fr.aoc.session2024;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import static fr.aoc.common.Utils.NUMBER_PATTERN;

@Slf4j
public class Day02 {

    public static void main(String[] args) throws IOException {
        var today = new Day02();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2024/day02/input").stream()
                .map(line -> NUMBER_PATTERN.matcher(line).results())
                .map(matchResultStream -> matchResultStream.map(MatchResult::group).mapToLong(Long::parseLong).boxed().toList())
                .toList();

        var safeReports = input.stream()
                .filter(today::isReportSafe)
                .count();

        log.info("Number of safe reports: {}", safeReports);

        var tolerableReports = input.stream()
                .filter(today::isReportTolerable)
                .count();
        log.info("Number of tolerableReports: {}", tolerableReports);
    }

    private boolean isReportTolerable(List<Long> report) {
        if (isReportSafe(report)) return true;

        for (int index = 0; index < report.size(); index++) {
            ArrayList<Long > subReport = new ArrayList<>();
            subReport.addAll(report.subList(0, index));
            subReport.addAll(report.subList(index + 1, report.size()));
            if (isReportSafe(subReport)) return true;
        }

        return false;
    }

    private boolean isReportSafe(List<Long> report) {
        var ascending = report.get(1) - report.get(0) > 0;
        for (var index = 0; index < report.size() - 1; index++) {
            var diff = report.get(index + 1) - report.get(index);
            if (Math.abs(diff) > 3 || Math.abs(diff) < 1) {
                return false;
            }

            if ((ascending && diff < 0) || (!ascending && diff > 0)) {
                return false;
            }
        }
        return true;
    }

}
