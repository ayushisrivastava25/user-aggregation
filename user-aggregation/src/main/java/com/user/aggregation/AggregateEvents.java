package com.user.aggregation;

import com.user.aggregation.model.OutputUserDetail;
import com.user.aggregation.model.UserDetail;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.support.util.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@ShellComponent
public class AggregateEvents {

    @ShellMethod(key = "aggregate-events")
    public String aggregateEvents(
            @CliOption(key = {"in", "file"}) String inputFile,
            @CliOption(key = {"out", "file"}) String outputFile,
            @CliOption(key = "update") String updateFile) {
        String output = "[\n";
        if (updateFile.equals("update")) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
                List<UserDetail> userDetails = getUserDetails(br);
                Map<String, List<UserDetail>> userDetailsMap = getUserDetailsMap(userDetails);
                List<OutputUserDetail> outputUserDetails = new ArrayList<>();

                try {
                    BufferedReader outputBr = new BufferedReader(new FileReader(outputFile));
                    outputUserDetails = getOutputUserDetails(outputUserDetails, outputBr);
                    outputUserDetails = getOutputUserDetails(outputUserDetails, userDetailsMap);
                    output = getOutputFromList(output, outputUserDetails);
                } catch (FileNotFoundException e) {
                    return "Error in Output file : " + outputFile + " not found";
                } catch (IOException e) {
                    return "Error in Output file parsing";
                }
            } catch (FileNotFoundException e) {
                return "Error in Input file : " + inputFile + " not found";
            } catch (IOException e) {
                return "Error in Input file parsing";
            }
            if (StringUtils.isEmpty(output)) {
                output += "[]";
            }
            String msg = printOutputFile(outputFile, output);
            if (msg != null) return msg;
            return "Updated";
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
                List<UserDetail> userDetails = getUserDetails(br);
                Map<String, List<UserDetail>> userDetailsMap = getUserDetailsMap(userDetails);

                List<OutputUserDetail> outputUserDetails = getOutputUserDetails(userDetailsMap);
                output = getOutputFromList(output, outputUserDetails);
            } catch (FileNotFoundException e) {
                return "Error in Input file : " + inputFile + " not found";
            } catch (IOException e) {
                return "Error in Input file parsing";
            }
            if (StringUtils.isEmpty(output)) {
                output += "[]";
            }
            String msg = printOutputFile(outputFile, output);
            if (msg != null) return msg;
            return "Created";
        }
    }

    private List<OutputUserDetail> getOutputUserDetails(
            Map<String, List<UserDetail>> userDetailsMap) {
        List<OutputUserDetail> outputUserDetails = new ArrayList<>();
        String[] keys;
        if (!CollectionUtils.isEmpty(userDetailsMap.entrySet())) {
            int postCount;
            int commentCount;
            int likeCount;
            for (Map.Entry<String, List<UserDetail>> entry : userDetailsMap.entrySet()) {
                keys = entry.getKey().split("_");
                postCount = 0;
                commentCount = 0;
                likeCount = 0;
                for (UserDetail userDetail : entry.getValue()) {
                    switch (userDetail.getEventName()) {
                        case "post":
                            postCount++;
                            break;
                        case "comment":
                            commentCount++;
                            break;
                        case "likeReceived":
                            likeCount++;
                            break;
                        default:
                            break;
                    }
                }
                outputUserDetails.add(
                        OutputUserDetail.builder()
                                .userId(Long.valueOf(keys[1]))
                                .date(keys[0])
                                .post(postCount)
                                .comment(commentCount)
                                .likesReceived(likeCount)
                                .build());
            }
        }
        return outputUserDetails;
    }

    private List<OutputUserDetail> getOutputUserDetails(
            List<OutputUserDetail> outputUserDetails,
            Map<String, List<UserDetail>> userDetailsMap) {
        List<OutputUserDetail> finalList = new ArrayList<>();
        String[] keys;
        if (!CollectionUtils.isEmpty(userDetailsMap.entrySet())) {
            int postCount;
            int commentCount;
            int likeCount;
            for (Map.Entry<String, List<UserDetail>> entry : userDetailsMap.entrySet()) {
                keys = entry.getKey().split("_");
                postCount = 0;
                commentCount = 0;
                likeCount = 0;
                for (UserDetail userDetail : entry.getValue()) {
                    switch (userDetail.getEventName()) {
                        case "post":
                            postCount++;
                            break;
                        case "comment":
                            commentCount++;
                            break;
                        case "likeReceived":
                            likeCount++;
                            break;
                        default:
                            break;
                    }
                }
                if (!CollectionUtils.isEmpty(outputUserDetails)) {
                    for (OutputUserDetail outputUserDetail : outputUserDetails) {
                        if (outputUserDetail.getUserId().equals(Long.valueOf(keys[1]))
                                && outputUserDetail.getDate().equals(keys[0])) {
                            finalList.add(
                                    OutputUserDetail.builder()
                                            .userId(Long.valueOf(keys[1]))
                                            .date(keys[0])
                                            .post(outputUserDetail.getPost() + postCount)
                                            .comment(outputUserDetail.getComment() + commentCount)
                                            .likesReceived(
                                                    outputUserDetail.getLikesReceived() + likeCount)
                                            .build());
                        }
                    }
                } else {
                    finalList.add(
                            OutputUserDetail.builder()
                                    .userId(Long.valueOf(keys[1]))
                                    .date(keys[0])
                                    .post(postCount)
                                    .comment(commentCount)
                                    .likesReceived(likeCount)
                                    .build());
                }
            }
        }
        return finalList;
    }

    private List<OutputUserDetail> getOutputUserDetails(
            List<OutputUserDetail> outputUserDetails, BufferedReader br) throws IOException {
        String st;
        while ((st = br.readLine()) != null) {
            if (!(st.equals("[]")) && !(st.equals("[")) && !(st.equals("]"))) {
                outputUserDetails.add(
                        OutputUserDetail.builder()
                                .userId(Long.parseLong(st.split("\"userId\": ")[1].split(",")[0]))
                                .date(st.split("\"date\": ")[1].split(",")[0].replace("\"", ""))
                                .post(
                                        st.split("\"post\": ").length > 1
                                                ? Integer.parseInt(
                                                        String.valueOf(
                                                                st.split("\"post\": ")[1].charAt(
                                                                        0)))
                                                : 0)
                                .comment(
                                        st.split("\"comment\": ").length > 1
                                                ? Integer.parseInt(
                                                        String.valueOf(
                                                                st.split("\"comment\": ")[1].charAt(
                                                                        0)))
                                                : 0)
                                .likesReceived(
                                        st.split("\"likesReceived\": ").length > 1
                                                ? Integer.parseInt(
                                                        String.valueOf(
                                                                st.split("\"likesReceived\": ")[1]
                                                                        .charAt(0)))
                                                : 0)
                                .build());
            }
        }
        return outputUserDetails;
    }

    private String getOutputFromList(String output, List<OutputUserDetail> outputUserDetails) {
        int counter = 0;
        for (OutputUserDetail outputUserDetail : outputUserDetails) {
            output +=
                    " {\"userId\": "
                            + outputUserDetail.getUserId()
                            + ", \"date\": \""
                            + outputUserDetail.getDate()
                            + "\"";
            if (outputUserDetail.getPost() > 0) {
                output += ", \"post\": " + outputUserDetail.getPost();
            }
            if (outputUserDetail.getComment() > 0) {
                output += ", \"comment\": " + outputUserDetail.getComment();
            }
            if (outputUserDetail.getLikesReceived() > 0) {
                output += ", \"likesReceived\": " + outputUserDetail.getLikesReceived();
            }
            if (counter == outputUserDetails.size() - 1) {
                output += "}\n";
            } else {
                output += "},\n";
            }
            counter++;
        }
        output += "]";
        return output;
    }

    private List<UserDetail> getUserDetails(BufferedReader br) throws IOException {
        List<UserDetail> userDetails = new ArrayList<>();
        String st;
        while ((st = br.readLine()) != null) {
            if ((!st.equals("[")) && (!st.equals("]"))) {
                userDetails.add(
                        UserDetail.builder()
                                .userId(st.split(": ")[1].split(",")[0])
                                .eventName(st.split(": ")[2].split(",")[0].replace("\"", ""))
                                .timestamp(st.split(": ")[3].split("}")[0])
                                .build());
            }
        }
        return userDetails;
    }

    private List<UserDetail> getUserDetails(BufferedReader br, List<UserDetail> userDetails)
            throws IOException {
        String st;
        while ((st = br.readLine()) != null) {
            if ((!st.equals("[]")) && (!st.equals("[")) && (!st.equals("]"))) {
                userDetails.add(
                        UserDetail.builder()
                                .userId(st.split(": ")[1].split(",")[0])
                                .eventName(st.split(": ")[2].split(",")[0].replace("\"", ""))
                                .timestamp(st.split(": ")[3].split("}")[0])
                                .build());
            }
        }
        return userDetails;
    }

    private Map<String, List<UserDetail>> getUserDetailsMap(List<UserDetail> userDetails) {
        Map<String, List<UserDetail>> userDetailsMap = new HashMap<>();
        for (UserDetail userDetail : userDetails) {
            if (!StringUtils.isEmpty(userDetail.getTimestamp())) {
                Date date = new Date(Long.parseLong(userDetail.getTimestamp()) * 1000l);
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                String formattedDate = format.format(date);
                userDetail.setFormattedDate(formattedDate);
                String key = userDetail.getFormattedDate() + "_" + userDetail.getUserId();
                if (userDetailsMap.containsKey(key)) {
                    List<UserDetail> existingUserDetails = new ArrayList<>(userDetailsMap.get(key));
                    existingUserDetails.add(userDetail);
                    userDetailsMap.put(key, existingUserDetails);
                } else {
                    userDetailsMap.put(key, List.of(userDetail));
                }
            }
        }
        return userDetailsMap;
    }

    private String printOutputFile(String outputFile, String output) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(outputFile);
            out.write(output);
        } catch (FileNotFoundException e) {
            return "Error in Output file : " + outputFile + " not found";
        } finally {
            out.close();
        }
        return null;
    }
}
