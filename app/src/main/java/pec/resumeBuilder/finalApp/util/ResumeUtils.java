package pec.resumeBuilder.finalApp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.TextUtils;
import android.webkit.WebView;

import com.sultanofcardio.resumebuildersultanofcardio.R;
import pec.resumeBuilder.finalApp.models.Education;
import pec.resumeBuilder.finalApp.models.Experience;
import pec.resumeBuilder.finalApp.models.Reference;
import pec.resumeBuilder.finalApp.models.Resume;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * @author namit
 */

public class ResumeUtils {

    public static String createResume(Resume resume, Context context){
        int templateNumber = resume.getResumeType();
        String html;
        switch (templateNumber){
            case 1:
                html = createResume1(resume, context);
                break;
            case 2:
                html = createResume2(resume, context);
                break;
            default:
                html = createResume1(resume, context);
        }

        return html;
    }

    public static String createResume1(Resume resume, Context context){
        String html = "";
        String prefix = "resume_1";
        String template = prefix + ".html";
        try {
            Document document = Jsoup.parse(context.getAssets().open(template), "UTF-8", "");
            document.getElementById("name").html(resume.getName());
            if(!TextUtils.isEmpty(resume.getThumbnail()))
                document.getElementById("thumbnail").attr("src", resume.getThumbnail());
            else
                document.getElementById("thumbnail").remove();
            document.getElementById("number").html(resume.getNumber());
            document.getElementById("email").attr("href", "mailto:" + resume.getEmail()).html(resume.getEmail());
            if(!TextUtils.isEmpty(resume.getWebsite()))
                document.getElementById("website").attr("href", "http://" + resume.getWebsite()).html(resume.getWebsite());
            else {
                document.getElementById("site").remove();
                document.getElementById("website").remove();
            }
            document.getElementById("address").html(resume.getAddress());
            document.getElementById("summary").html(resume.getSummary());

            for(Experience experience: resume.getExperiences()) {
                String experienceTemplate = prefix + "_experience.html";
                Document experienceDoc = Jsoup.parse(context.getAssets().open(experienceTemplate), "UTF-8", "");
                Element role = experienceDoc.getElementById("role").html(experience.getRole());
                Element startMonth = experienceDoc.getElementById("start_month").html(experience.getStartMonth());
                Element startYear = experienceDoc.getElementById("start_year").html(experience.getStartYear());
                Element endMonth = experienceDoc.getElementById("end_month").html(experience.getEndMonth());
                Element endYear = experienceDoc.getElementById("end_year").html(experience.getEndYear());
                Element company = experienceDoc.getElementById("company");
                company.html(experience.getCompany())
                        .append(" " +  role.outerHtml())
                        .append(" " +  startMonth.outerHtml())
                        .append(" " +  startYear.outerHtml())
                        .append(" - " +  endMonth.outerHtml())
                        .append(" " +  endYear.outerHtml());
                experienceDoc.getElementById("description").html(experience.getDescription());
                document.getElementById("experience").append(experienceDoc.outerHtml());
            }

            for(Education education: resume.getEducation()) {
                String educationTemplate = prefix + "_education.html";
                Document educationDoc = Jsoup.parse(context.getAssets().open(educationTemplate), "UTF-8", "");
                educationDoc.getElementById("institution").html(education.getInstitution());
                educationDoc.getElementById("major").html(education.getMajor());
                educationDoc.getElementById("gpa").html(education.getGpa());
                document.getElementById("education").append(educationDoc.outerHtml());
            }

            for(String skill: resume.getSkills()) {
                document.getElementById("skills").append(new Element("li").html(skill).outerHtml());
            }

            for(Reference reference: resume.getReferenceList()) {
                String referenceTemplate = prefix + "_reference.html";
                Document referenceDoc = Jsoup.parse(context.getAssets().open(referenceTemplate), "UTF-8", "");
                Element name = referenceDoc.getElementById("name");
                Element title = referenceDoc.getElementById("title");
                title.html(reference.getTitle());
                name.html(reference.getName()).append(" " + title.outerHtml());
                referenceDoc.getElementById("number").html(reference.getNumber());
                referenceDoc.getElementById("email").attr("href", "mailto:" + reference.getEmail()).html(reference.getEmail());
                document.getElementById("references").append(referenceDoc.outerHtml());
            }

            String stylesheet = prefix + ".css";
            document.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", stylesheet);
            html = document.outerHtml();
        } catch (IOException e){
            e.printStackTrace();
        }

        return html;
    }

    public static String createResume2(Resume resume, Context context){
        String html = "";
        String template = "resume_2.html";
        try {
            Document document = Jsoup.parse(context.getAssets().open(template), "UTF-8", "");
            document.getElementById("profile-name").html(resume.getName());
            if(!TextUtils.isEmpty(resume.getThumbnail()))
                document.getElementById("profile-image").attr("style", "background-image: url('"+ resume.getThumbnail() +"');");
//            else
//                document.getElementById("thumbnail").remove();
            document.getElementById("profile-summary").html(resume.getSummary());
            document.getElementById("profile-phone-number").html(resume.getNumber());
            document.getElementById("profile-email").attr("href", "mailto:" + resume.getEmail()).html(resume.getEmail());
            document.getElementById("profile-address").html(resume.getAddress());

            for(String skill: resume.getSkills()) {
                String skillTemplate = "resume_2_skill.html";
                Document skillDoc = Jsoup.parse(context.getAssets().open(skillTemplate), "UTF-8", "");
                skillDoc.getElementById("profile-skill").html(skill);
                document.getElementById("profile-skills-title").parent()
                        .append(skillDoc.outerHtml());
            }

            String experienceCard = "resume_2_experience_card.html";
            Document experienceCardDoc = Jsoup.parse(context.getAssets().open(experienceCard), "UTF-8", "");
            for(Experience experience: resume.getExperiences()) {
                String experienceTemplate = "resume_2_experience.html";
                Document experienceDoc = Jsoup.parse(context.getAssets().open(experienceTemplate), "UTF-8", "");


                experienceDoc.getElementById("work-experience-job-title").html(experience.getRole());
                experienceDoc.getElementById("work-experience-job-place").html("@" + experience.getCompany());
                experienceDoc.getElementById("work-experience-duration-from").html(
                        experience.getStartMonth() + " " + experience.getStartYear());
                experienceDoc.getElementById("work-experience-duration-to").html(
                        experience.getEndMonth() + " " + experience.getEndYear());
                experienceDoc.getElementById("work-experience-description").html(experience.getDescription());
                experienceCardDoc.getElementById("work-experience-list").append(experienceDoc.outerHtml());
            }

            if(resume.getExperiences().size() > 0)
                document.getElementById("right-card").append(experienceCardDoc.outerHtml());

            String educationCard = "resume_2_education_card.html";
            Document educationCardDoc = Jsoup.parse(context.getAssets().open(educationCard), "UTF-8", "");
            for(Education education: resume.getEducation()) {
                String educationTemplate = "resume_2_education.html";
                Document educationDoc = Jsoup.parse(context.getAssets().open(educationTemplate), "UTF-8", "");

                educationDoc.getElementById("education-school-title").html(education.getMajor());
                educationDoc.getElementById("education-school-place").html("@" + education.getInstitution());
                educationCardDoc.getElementById("education-list").append(educationDoc.outerHtml());
            }

            if(resume.getEducation().size() > 0)
                document.getElementById("right-card").append(educationCardDoc.outerHtml());

            String referenceCard = "resume_2_reference_card.html";
            Document referenceCardDoc = Jsoup.parse(context.getAssets().open(referenceCard), "UTF-8", "");
            for(Reference reference: resume.getReferenceList()) {
                String referenceTemplate = "resume_2_reference.html";
                Document referenceDoc = Jsoup.parse(context.getAssets().open(referenceTemplate), "UTF-8", "");

                referenceDoc.getElementById("reference-person-name").html(reference.getName());
                referenceDoc.getElementById("reference-person-title").html(reference.getTitle());
                referenceDoc.getElementById("reference-phone-number").html(reference.getNumber());
                referenceDoc.getElementById("reference-email").html(reference.getEmail());
                referenceCardDoc.getElementById("reference-list").append(referenceDoc.outerHtml());
            }

            if(resume.getReferenceList().size() > 0)
                document.getElementById("right-card").append(referenceCardDoc.outerHtml());

            if(!TextUtils.isEmpty(resume.getWebsite())) {
                String websiteCard = "resume_2_website_card.html";
                Document websiteCardDoc = Jsoup.parse(context.getAssets().open(websiteCard), "UTF-8", "");
                websiteCardDoc.getElementById("website").attr("href", "http://" + resume.getWebsite()).html(resume.getWebsite());
                document.getElementById("right-card").append(websiteCardDoc.outerHtml());
            }

            html = document.outerHtml();
        } catch (IOException e){
            e.printStackTrace();
        }

        return html;
    }

    public static void printResume(WebView webView, Context context){
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            printAdapter = webView.createPrintDocumentAdapter("Resume");
        } else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            printAdapter = webView.createPrintDocumentAdapter();
        }

        String jobName = context.getString(R.string.app_name) + " Resume";
        PrintJob printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    public static void takeSnapshot(Bitmap bitmap, Context context, Resume resume){

        FileOutputStream outputStream = null;
        try{
            File file = FileUtils.createFile(String.format(Locale.US, "%d_snapshot.png",
                    resume.getId()), FileUtils.createDirectory("Photos", context), context);
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
