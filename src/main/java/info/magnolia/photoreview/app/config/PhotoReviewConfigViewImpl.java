/**
 * This file Copyright (c) 2013-2014 Magnolia International
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
package info.magnolia.photoreview.app.config;

import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.vaadin.form.FormSection;
import info.magnolia.ui.vaadin.layout.SmallAppLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

/**
 * Default Vaadin implementation of the {@link PhotoReviewConfigView}.
 */
public class PhotoReviewConfigViewImpl implements PhotoReviewConfigView {

    protected final SmallAppLayout root = new SmallAppLayout();

    private PhotoReviewConfigView.Listener listener;

    private FormLayout formLayout;
    private Item dataSource;
    private List<Field<?>> fields = new ArrayList<Field<?>>();


    private final SimpleTranslator i18n;

    @Inject
    public PhotoReviewConfigViewImpl(SimpleTranslator i18n) {
        this.i18n = i18n;
        root.setDescription(i18n.translate("photoreview.config.description"));
        root.addSection(createFormLayout());
    }

    private Component createFormLayout() {

        formLayout = new FormLayout();

        Label sectionTitle = new Label(i18n.translate("photoreview.config.title"));
        sectionTitle.addStyleName("section-title");
        formLayout.addComponent(sectionTitle);

        Button saveButton = new Button(i18n.translate("photoreview.main.saveSettings.label"));
        saveButton.addStyleName("commit");
        saveButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                listener.saveSettings(dataSource);
            }
        });
        formLayout.addComponent(saveButton);

        return formLayout;
    }

    @Override
    public void setListener(PhotoReviewConfigView.Listener listener) {
        this.listener = listener;
    }

    @Override
    public Component asVaadinComponent() {
        return root;
    }

    // IMPLEMENTATION OF FORMVIEW INTERFACE

    @Override
    public void addField(Field<?> field) {
        fields.add(field);
        formLayout.addComponent(field, formLayout.getComponentCount() > 0 ? formLayout.getComponentCount() - 1 : 0);
    }

    @Override
    public void setDescriptionVisibility(boolean isVisible) {
    }

    @Override
    public void addFormSection(String tabName, FormSection inputFields) {
    }

    @Override
    public void showValidation(boolean isVisible) {
    }

    @Override
    public void setShowAllEnabled(boolean enabled) {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Collection<Field<?>> getFields() {
        return fields;
    }

    @Override
    public void setItemDataSource(Item newDataSource) {
        this.dataSource = newDataSource;
    }

    @Override
    public Item getItemDataSource() {
        return dataSource;
    }

}
