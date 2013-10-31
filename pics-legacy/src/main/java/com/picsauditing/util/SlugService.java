package com.picsauditing.util;

import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SlugService {
    @Autowired
    public BasicDAO basicDao;

    public <T extends BaseTable> String generateSlug(Class<T> clazz, String slugable, int id) throws Exception {
        String slug = createSlugFromText(slugable);
        validateSlug(clazz, slug, id);
        return slug;
    }

    private String createSlugFromText(String slugable) {
        String lowercase = slugable.toLowerCase();
        String noHyphens = lowercase.replaceAll("-", "");
        String spacesToHyphens = noHyphens.replaceAll("\\s+", "-");
        String slug = spacesToHyphens.replaceAll("[^A-Za-z0-9-_]+", "");

        return slug;
    }

    private <T extends BaseTable> void validateSlug(Class<T> clazz, String slug, int id) throws Exception {
        if (slugHasDuplicate(clazz, slug, id)) {
            throw new SlugFormatException("Slug Already Exists");
        }
        else if (!slugIsURICompliant(slug)) {
            throw new SlugFormatException("Slug is not URI Compliant");
        }
    }

    public <T extends BaseTable> boolean slugHasDuplicate(Class<T> clazz, String slug, int id) {
        List<T> list = basicDao.findBySlug(clazz, slug);

        if (list.isEmpty() || (list.size() == 1 && list.get(0).getId() == id)) {
            return false;
        }

        return true;
    }

    public boolean slugIsURICompliant(String slug) {
        return slug.matches("[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }
}