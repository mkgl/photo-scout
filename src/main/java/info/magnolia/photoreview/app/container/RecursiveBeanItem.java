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

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;

/**
 * The {@link RecursiveBeanItem} allows to get item properties from nested members of the current bean, using a dot-separated key.
 * <p>
 * For example, having a <code>Media</code> bean containing a <code>Caption</code> POJO, itself having a <code>text</code> property (String), the latter can be requested from the root bean using the following key: <code>"caption.text"</code>.<br/>
 */
public class RecursiveBeanItem<BT> extends BeanItem<BT> {

    private static final String SEPARATOR = ".";

    public RecursiveBeanItem(BT bean) {
        super(bean);
    }

    @Override
    public Property<?> getItemProperty(Object id) {
        if (id instanceof String && ((String) id).contains(SEPARATOR)) {
            String thisKey = StringUtils.substringBefore((String) id, SEPARATOR);
            String nextKey = StringUtils.substringAfter((String) id, SEPARATOR);
            Property<?> property = super.getItemProperty(thisKey);
            Object subBean = property.getValue();
            if (subBean != null) {
                return new RecursiveBeanItem(subBean).getItemProperty(nextKey);
            } else {
                return null;
            }
        }
        return super.getItemProperty(id);
    }

}
