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

import static info.magnolia.photoreview.PhotoReviewConstants.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.task.Task;
import info.magnolia.task.TasksManager;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * The SubmitPhotoAction.
 */
public class SubmitPhotoAction extends AbstractAction<SubmitPhotoActionDefinition> {

    private static final Logger log = LoggerFactory.getLogger(SubmitPhotoAction.class);

    private final Item dialogItem;
    private final EditorValidator validator;
    private final EditorCallback callback;
    private final TasksManager taskManager;
    private final ContentConnector contentConnector;
    private final UiContext uiContext;
    private final SimpleTranslator i18n;

    @Inject
    public SubmitPhotoAction(SubmitPhotoActionDefinition definition, Item dialogItem, EditorValidator validator, EditorCallback callback,
            TasksManager taskManager, ContentConnector contentConnector, UiContext uiContext, SimpleTranslator i18n) {
        super(definition);
        this.dialogItem = dialogItem;
        this.validator = validator;
        this.callback = callback;
        this.taskManager = taskManager;
        this.contentConnector = contentConnector;
        this.uiContext = uiContext;
        this.i18n = i18n;
    }

    @Override
    public void execute() throws ActionExecutionException {

        validator.showValidation(true);
        if (validator.isValid()) {

            Task task = new Task();
            task.setName(TASK_NAME);

            Object itemId = dialogItem.getItemProperty("itemId").getValue();
            Item item = contentConnector.getItem(itemId);

            Map<String, Object> content = new HashMap<String, Object>();
            content.put("itemId", itemId);
            content.put("link", item.getItemProperty("link").getValue());
            content.put("images.standardResolution.url", item.getItemProperty("images.standardResolution.url").getValue());

            task.setContent(content);

            task.setComment(String.valueOf(dialogItem.getItemProperty("subject").getValue()));

            // set sender to current user, assign to photo editors
            task.setRequestor(MgnlContext.getUser().getName());
            task.setGroupIds(Arrays.asList(GROUP_PHOTO_EDITORS));

            taskManager.addTask(task);

            callback.onSuccess(getDefinition().getName());
            uiContext.openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("photo-review.task.actions.commit.success"));

        } else {
            log.info("Validation error(s) occurred. No save performed.");
        }
    }

}
