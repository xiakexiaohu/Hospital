
import com.gs.bean.ContactInfo;
import com.gs.utils.FileUtils;
import junit.framework.TestCase;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by WangGenshen on 11/28/16.
 */
public class ProductParserTest extends TestCase {

    public static void main(String[] args) throws IOException, ParseException {
        List<String> strings = FileUtils.readFromFile("C:\\0a\\workspace\\IdeaProjects\\publiccode\\out\\artifacts\\publiccode_war_exploded\\WEB-INF\\contact\\contact.txt");
        List<ContactInfo> contacts = FileUtils.getContacts(strings);
        System.out.println(1);

        List<String> revisitors = FileUtils.getRevisitors("C:\\0a\\workspace\\IdeaProjects\\publiccode\\out\\artifacts\\publiccode_war_exploded\\WEB-INF\\revisit");

        System.out.println(1);
    }
}
