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
package info.magnolia.photoreview.app.action;

import info.magnolia.photoreview.app.container.InstagramContainerConfigurer;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.form.action.SaveFormActionDefinition;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import javax.inject.Inject;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * The SaveConfigSettingsAction.
 */
public class SaveConfigSettingsAction extends AbstractAction<SaveFormActionDefinition> {

    private final Item item;
    private final InstagramContainerConfigurer configurer;
    private final UiContext uiContext;

    @Inject
    public SaveConfigSettingsAction(SaveFormActionDefinition definition, Item item, InstagramContainerConfigurer configurer, UiContext uiContext) {
        super(definition);
        this.item = item;
        this.configurer = configurer;
        this.uiContext = uiContext;
    }

    @Override
    public void execute() throws ActionExecutionException {
        configurer.setFeedType((String) item.getItemProperty("feedType").getValue());
        Property<?> tag = item.getItemProperty("feedTypetag");
        if (tag != null) {
            configurer.setTagName((String) tag.getValue());
        }
        Property<?> user = item.getItemProperty("feedTypeuser");
        if (user != null) {
            configurer.setUserName((String) user.getValue());
        }
        uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Settings updated.");
    }

}
