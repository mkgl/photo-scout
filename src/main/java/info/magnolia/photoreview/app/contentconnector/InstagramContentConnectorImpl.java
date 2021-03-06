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
package info.magnolia.photoreview.app.contentconnector;

import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.photoreview.app.container.InstagramContainer;
import info.magnolia.photoreview.app.container.InstagramContainerConfigurer;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

/**
 * The InstagramContentConnectorImpl.
 */
public class InstagramContentConnectorImpl implements ContentConnector, InstagramContentConnector {

    private static final Logger log = LoggerFactory.getLogger(InstagramContentConnectorImpl.class);

    private static final String INSTAGRAM_CLIENT_ID = "instagram.oauth.clientId";
    private static final String INSTAGRAM_CLIENT_SECRET = "instagram.oauth.clientSecret";
    private static final String INSTAGRAM_ACCESSTOKEN = "instagram.oauth.accessToken";

    private final InstagramContainer container;

    @Inject
    public InstagramContentConnectorImpl(MagnoliaConfigurationProperties magnoliaProperties, InstagramContainerConfigurer configurer) {
        String clientId = magnoliaProperties.getProperty(INSTAGRAM_CLIENT_ID);
        String clientSecret = magnoliaProperties.getProperty(INSTAGRAM_CLIENT_SECRET);
        String accessToken = magnoliaProperties.getProperty(INSTAGRAM_ACCESSTOKEN);
        if (StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret) && StringUtils.isNotBlank(accessToken)) {
            configurer.setClientId(clientId);
            configurer.setClientSecret(clientSecret);
            configurer.setAccessToken(accessToken);
        } else {
            log.warn("Cannot establish connection to Instagram's API; OAuth credentials are missing in magnolia.properties. Please set the following three properties:\n\t{}\n\t{}\n\t{}",
                    INSTAGRAM_CLIENT_ID, INSTAGRAM_CLIENT_SECRET, INSTAGRAM_ACCESSTOKEN);
        }
        container = new InstagramContainer(configurer);
    }

    @Override
    public String getItemUrlFragment(Object itemId) {
        return String.valueOf(itemId);
    }

    @Override
    public Object getItemIdByUrlFragment(String urlFragment) {
        return urlFragment;
    }

    @Override
    public Object getDefaultItemId() {
        return null;
    }

    @Override
    public Item getItem(Object itemId) {
        return container.getItem(itemId);
    }

    @Override
    public Object getItemId(Item item) {
        if (item instanceof BeanItem) {
            return item.getItemProperty("id").getValue();
        }
        return null;
    }

    @Override
    public boolean canHandleItem(Object itemId) {
        return itemId instanceof String && ((String) itemId).matches("^[0-9]*_[0-9]*$");
    }

    // NOT PART OF THE CONTENT CONNECTOR INTERFACE

    @Override
    public Container getContainer() {
        return container;
    }

}
