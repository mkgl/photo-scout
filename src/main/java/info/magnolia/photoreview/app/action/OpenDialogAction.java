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

import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenter;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenterFactory;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

/**
 * Opens a dialog for filling a form.
 *
 * @see OpenDialogActionDefinition
 */
public class OpenDialogAction extends AbstractAction<OpenDialogActionDefinition> {

    private final Item itemToEdit;
    private final FormDialogPresenterFactory formDialogPresenterFactory;
    private final UiContext uiContext;
    private final SimpleTranslator i18n;
    private final ContentConnector contentConnector;

    @Inject
    public OpenDialogAction(OpenDialogActionDefinition definition, Item itemToEdit, FormDialogPresenterFactory formDialogPresenterFactory, UiContext uiContext, SimpleTranslator i18n, ContentConnector contentConnector) {
        super(definition);
        this.itemToEdit = itemToEdit;
        this.formDialogPresenterFactory = formDialogPresenterFactory;
        this.uiContext = uiContext;
        this.contentConnector = contentConnector;
        this.i18n = i18n;
    }

    @Override
    public void execute() throws ActionExecutionException {

        // check for definition/dialog existence
        String dialogName = getDefinition().getDialogName();
        if(StringUtils.isBlank(dialogName)){
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, false, i18n.translate("ui-framework.actions.no.dialog.definition", getDefinition().getName()));
            return;
        }

        final FormDialogPresenter formDialogPresenter = formDialogPresenterFactory.createFormDialogPresenter(dialogName);
        if(formDialogPresenter == null){
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, false, i18n.translate("ui-framework.actions.dialog.not.registered", dialogName));
            return;
        }

        // initialize vaadin item to bind properties to
        Object itemId = contentConnector.getItemId(itemToEdit);
        Item item = getItemForEditing();
        item.addItemProperty("itemId", new ObjectProperty<Object>(itemId));

        // open dialg and register callbacks
        formDialogPresenter.start(item, getDefinition().getDialogName(), uiContext, new EditorCallback() {

            @Override
            public void onSuccess(String actionName) {
                formDialogPresenter.closeDialog();
            }

            @Override
            public void onCancel() {
                formDialogPresenter.closeDialog();
            }
        });
    }

    protected Item getItemForEditing() {
        return new PropertysetItem();
    }
}
