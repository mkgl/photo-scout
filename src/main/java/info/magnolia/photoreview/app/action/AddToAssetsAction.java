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

import static info.magnolia.photoreview.PhotoReviewConstants.DAM_FOLDER_NAME;

import info.magnolia.cms.beans.config.MIMEMapping;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.task.Task;
import info.magnolia.task.TasksManager;
import info.magnolia.ui.admincentral.shellapp.pulse.task.DefaultTaskDetailPresenter;
import info.magnolia.ui.admincentral.shellapp.pulse.task.action.ResolveTaskAction;
import info.magnolia.ui.admincentral.shellapp.pulse.task.action.ResolveTaskActionDefinition;
import info.magnolia.ui.api.shell.Shell;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.value.ValueFactoryImpl;

/**
 * The AddToAssetsAction.
 */
public class AddToAssetsAction extends ResolveTaskAction {

    private final Task task;

    @Inject
    public AddToAssetsAction(ResolveTaskActionDefinition definition, Task task, TasksManager taskManager, DefaultTaskDetailPresenter taskPresenter, Shell shell) {
        super(definition, task, taskManager, taskPresenter, shell);
        this.task = task;
    }

    @Override
    protected void executeTask(TasksManager taskManager, Task task) {
        // first resolve item properties from task
        String assetName = (String) task.getContent().get("itemId");
        String urlString = (String) task.getContent().get("images.standardResolution.url");
        String extension = StringUtils.substringAfterLast(urlString, ".");

        InputStream inputStream = null;
        try {
            URL url = new URL((String) urlString);
            inputStream = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // then import to dam
        try {
            // create root folder if it does not exist
            Node damRoot = MgnlContext.getInstance().getJCRSession(DamConstants.WORKSPACE).getRootNode();
            Node folder = null;
            if (!damRoot.hasNode(DAM_FOLDER_NAME)) {
                folder = damRoot.addNode(DAM_FOLDER_NAME, NodeTypes.Folder.NAME);
            } else {
                folder = damRoot.getNode(DAM_FOLDER_NAME);
            }

            // create asset and binary property
            Node asset = folder.addNode(assetName, AssetNodeTypes.Asset.NAME);
            Node jcrContent = asset.addNode(JcrConstants.JCR_CONTENT, NodeTypes.Resource.NAME);
            Binary binary = ValueFactoryImpl.getInstance().createBinary(inputStream);
            jcrContent.setProperty(JcrConstants.JCR_DATA, binary);
            jcrContent.setProperty(JcrConstants.JCR_MIMETYPE, StringUtils.defaultIfEmpty(MIMEMapping.getMIMEType(extension), "application/octet-stream"));

            folder.getSession().save();

            // finally resolve task
            super.executeTask(taskManager, task);

        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

}
