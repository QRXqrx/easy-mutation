package nju.pa.experiment.util;

import nju.pa.experiment.data.diff.LocationDiff;
import nju.pa.experiment.data.diff.MutantLocation;
import nju.pa.experiment.data.mutation.MutantInfo;
import nju.pa.experiment.data.mutation.MutatedFileInfo;
import nju.pa.experiment.data.mutation.MutationResult;
import nju.pa.experiment.util.exception.CannotDiffException;

import java.util.ArrayList;
import java.util.List;

/**
 * This Util provides methods which are needed for diff.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-31
 */
public class MutationDiffUtil {

    private MutationDiffUtil() {}

    public static List<LocationDiff> diff(List<MutantLocation> locationList1, List<MutantLocation> locationList2) {
        List<LocationDiff>  locationDiffs = new ArrayList<>();

        // Add changed.
        for (MutantLocation location : locationList1)
            locationDiffs.addAll(diff(location, locationList2));

        // Add deleted
        for (MutantLocation location : locationList1)
            if(!locationList2.contains(location))
                locationDiffs.add(new LocationDiff(
                        location.getIndex(),
                        location.getStatus(),
                        false, true, false
                ));

        // Add added
        for (MutantLocation location : locationList2)
            if(!locationList1.contains(location))
                locationDiffs.add(new LocationDiff(
                        location.getIndex(),
                        location.getStatus(),
                        false, false, true
                ));

        return locationDiffs;
    }

    public static List<LocationDiff> diff(MutantLocation location, List<MutantLocation> locationList) {
        List<LocationDiff> locationDiffList = new ArrayList<>();

        for (MutantLocation location1 : locationList) {
            if(location.sameTo(location1)) {
                LocationDiff locationDiff = diff(location, location1);
                if(locationDiff.getIsChanged())
                    locationDiffList.add(locationDiff);
            }
        }

        return locationDiffList;
    }

    public static LocationDiff diff(MutantLocation location1, MutantLocation location2) {

        if(!location1.sameTo(location2)) {
            String msg = String.format(
                    "Two locations have different indices! Index1=[%s], Index2=[%s]",
                    location1.getIndex(),
                    location2.getIndex()
            );
            throw new CannotDiffException(msg);
        }

        String index = location1.getIndex();
        String diffStatus = location1.getStatus();
        Boolean isChanged = !location1.getStatus().equals(location2.getStatus());

        if(isChanged)
            diffStatus = formulateDiff(location1.getStatus(), location2.getStatus());

        return new LocationDiff(index, diffStatus, isChanged, false, false);
    }

    private static String formulateDiff(Object one, Object two) {
        return String.format("[%s]-[%s]", one.toString(), two.toString());
    }


    public static List<MutantLocation> mutationResultToMutantLocations(MutationResult result) {
        List<MutantLocation> locations = new ArrayList<>();

        List<MutatedFileInfo> mutatedFileInfos = result.getMutatedFileInfos();
        for (MutatedFileInfo mutatedFileInfo : mutatedFileInfos) {
            String packageInfo = mutatedFileInfo.getPackageInfo();
            String fileName = mutatedFileInfo.getFileName();

            List<MutantInfo> mutantInfos = mutatedFileInfo.getMutantInfos();
            for (MutantInfo mutantInfo : mutantInfos) {
                locations.add(new MutantLocation(packageInfo, fileName, mutantInfo));
            }
        }

        return locations;
    }

}
