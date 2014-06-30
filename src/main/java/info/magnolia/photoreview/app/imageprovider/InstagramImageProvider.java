/**
 * This file Copyright (c) 2014 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.photoreview.app.imageprovider;

import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import javax.inject.Inject;

import com.vaadin.data.Item;
import com.vaadin.server.ExternalResource;

/**
 * The InstagramImageProvider.
 */
public class InstagramImageProvider implements ImageProvider {

    private final ContentConnector contentConnector;

    @Inject
    public InstagramImageProvider(ContentConnector contentConnector) {
        this.contentConnector = contentConnector;
    }

    @Override
    public String getPortraitPath(Object itemId) {
        return null;
    }

    @Override
    public String getThumbnailPath(Object itemId) {
        return null;
    }

    @Override
    public String resolveIconClassName(String mimeType) {
        return "icon-file-image";
    }

    @Override
    public Object getThumbnailResource(Object itemId, String generator) {

        Item item = contentConnector.getItem(itemId);
        if (item != null) {
            String url = null;
            if (generator.equals("portrait")) {
                url = (String) item.getItemProperty("images.lowResolution.url").getValue();
            } else if (generator.equals("thumbnail")) {
                url = (String) item.getItemProperty("images.thumbnail.url").getValue();
            }

            if (url != null) {
                return new ExternalResource(url);
            }
        }
        return null;
    }

}
