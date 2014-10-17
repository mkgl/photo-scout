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
package info.magnolia.photoreview.app.container;

import info.magnolia.ui.workbench.container.Refreshable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.instagram4j.DefaultInstagramClient;
import org.instagram4j.InstagramClient;
import org.instagram4j.InstagramException;
import org.instagram4j.Parameter;
import org.instagram4j.Result;
import org.instagram4j.entity.Media;
import org.instagram4j.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;

/**
 * The InstagramContainer.
 */
public class InstagramContainer extends AbstractContainer implements Container.Indexed, Refreshable {

    private static final Logger log = LoggerFactory.getLogger(InstagramContainer.class);

    private static final String[] CONTAINER_PROPERTY_IDS = new String[] { "id", "caption.text", "location.name", "user.username", "filter", "type", "createdTimeAsDate" };

    private static final int DEFAULT_COUNT = 30;

    private final InstagramClient client;
    private final InstagramContainerConfigurer configurer;

    private String currentConfigurerState;

    private LinkedHashMap<String, Media> items;

    public InstagramContainer(InstagramContainerConfigurer configurer) {
        client = new DefaultInstagramClient(configurer.getClientId(), configurer.getClientSecret(), configurer.getAccessToken());
        this.configurer = configurer;
    }

    // CONTAINER READ OPERATIONS

    @Override
    public Item getItem(Object itemId) {
        if (itemId != null) {
            Media media = items.get(itemId);
            if (media != null) {
                return new RecursiveBeanItem<Media>(media);
            }
        }
        return null;
    }

    @Override
    public Collection<String> getContainerPropertyIds() {
        return Arrays.asList(CONTAINER_PROPERTY_IDS);
    }

    @Override
    public Collection<String> getItemIds() {
        if (items == null) {
            refreshMediaCache(DEFAULT_COUNT);
        }
        return items.keySet();
    }

    @Override
    public Property<?> getContainerProperty(Object itemId, Object propertyId) {
        Media media = items.get(itemId);
        if (media != null) {
            RecursiveBeanItem<Media> item = new RecursiveBeanItem<Media>(media);
            return item.getItemProperty(propertyId);
        }
        return null;
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return String.class;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean containsId(Object itemId) {
        return items.containsKey(itemId);
    }

    // REFRESHABLE IMPL

    @Override
    public void refresh() {
        refreshMediaCache(DEFAULT_COUNT);
    }

    private void refreshMediaCache(int count) {

        String state = String.format("%s:%s:%s", configurer.getFeedType(), configurer.getTagName(), configurer.getUserName());
        if (state.equals(currentConfigurerState)) {
            return;
        }
        currentConfigurerState = state;
        log.info("InstagramContainer configurer has changed, refreshing media cache.");

        items = new LinkedHashMap<String, Media>();
        try {
            Result<Media[]> result = null;
            Parameter param = Parameter.as("count", count);

            if (configurer.getFeedType() != null) {

                if (configurer.getFeedType().equals("tag") && StringUtils.isNotBlank(configurer.getTagName())) {
                    result = client.getRecentMediaForTag(configurer.getTagName(), param);

                } else if (configurer.getFeedType().equals("user") && StringUtils.isNotBlank(configurer.getUserName())) {
                    // resolve user id from name
                    Result<User[]> users = client.searchUsers(configurer.getUserName(), 1);
                    if (users.getData() != null && users.getData().length > 0) {
                        String userId = users.getData()[0].getId();
                        result = client.getRecentMediaForUser(userId, param);
                    }
                }
            }

            if (result == null) {
                String tag = configurer.getTagName();
                result = client.getRecentMediaForTag(tag, param);
                // result = client.getRecentMediaForUser(USER_ID, param);
                log.info("InstagramContainer configurer is not parameterized, default to tag '{}'.", tag);
            }

            if (result != null && result.getMeta().isSuccess()) {
                for (Media media : result.getData()) {
                    items.put(media.getId(), media);
                }
                fireItemSetChange();
            } else {
                log.warn("Could not get media from Instagram, check your connection/credentials details.");
            }

        } catch (InstagramException e) {
            e.printStackTrace();
        }
    }

    // CONTAINER WRITE OPERATIONS

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    // CONTAINER.INDEXED

    @Override
    public Object nextItemId(Object itemId) {
        List<String> keys = new ArrayList<String>(items.keySet());
        int index = keys.indexOf(itemId);
        if (index < keys.size() - 1) {
            return keys.get(index + 1);
        }
        return null;
    }

    @Override
    public Object prevItemId(Object itemId) {
        List<String> keys = new ArrayList<String>(items.keySet());
        int index = keys.indexOf(itemId);
        if (index > 0) {
            return keys.get(index - 1);
        }
        return null;
    }

    @Override
    public Object firstItemId() {
        List<String> keys = new ArrayList<String>(items.keySet());
        return keys.get(0);
    }

    @Override
    public Object lastItemId() {
        List<String> keys = new ArrayList<String>(items.keySet());
        return keys.get(keys.size() - 1);
    }

    @Override
    public boolean isFirstId(Object itemId) {
        List<String> keys = new ArrayList<String>(items.keySet());
        return keys.get(0).equals(itemId);
    }

    @Override
    public boolean isLastId(Object itemId) {
        List<String> keys = new ArrayList<String>(items.keySet());
        return keys.get(keys.size() - 1).equals(itemId);
    }

    @Override
    public int indexOfId(Object itemId) {
        List<String> keys = new ArrayList<String>(items.keySet());
        return keys.indexOf(itemId);
    }

    @Override
    public Object getIdByIndex(int index) {
        List<String> keys = new ArrayList<String>(items.keySet());
        return keys.get(index);
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        if (items == null) {
            refreshMediaCache(numberOfItems);
        }
        return new ArrayList<String>(items.keySet());
    }

    @Override
    public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

    @Override
    public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Container doesn't support write operations.");
    }

}
