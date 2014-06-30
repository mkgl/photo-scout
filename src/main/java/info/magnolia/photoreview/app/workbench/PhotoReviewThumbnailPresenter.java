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
package info.magnolia.photoreview.app.workbench;

import info.magnolia.event.EventBus;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.photoreview.app.container.InstagramContainer;
import info.magnolia.photoreview.app.contentconnector.InstagramContentConnector;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.workbench.thumbnail.ThumbnailContainer;
import info.magnolia.ui.workbench.thumbnail.ThumbnailPresenter;
import info.magnolia.ui.workbench.thumbnail.ThumbnailView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.vaadin.data.Container;

/**
 * The PhotoReviewThumbnailPresenter.
 */
public class PhotoReviewThumbnailPresenter extends ThumbnailPresenter {

    private final InstagramContentConnector contentConnector;

    @Inject
    public PhotoReviewThumbnailPresenter(ThumbnailView view, ImageProvider imageProvider, ComponentProvider componentProvider, ContentConnector contentConnector) {
        super(view, imageProvider, componentProvider);
        this.contentConnector = (InstagramContentConnector) contentConnector;
    }

    @Override
    public ThumbnailView start(WorkbenchDefinition workbench, EventBus eventBus, String viewTypeName, ContentConnector contentConnector) {
        ThumbnailView view = (ThumbnailView) super.start(workbench, eventBus, viewTypeName, contentConnector);
        view.setThumbnailSize(100, 100);
        return view;
    }

    @Override
    protected Container initializeContainer() {
        ThumbnailContainer c = new ThumbnailContainer(getImageProvider(), new ThumbnailContainer.IdProvider() {
            @Override
            public List<Object> getItemIds() {
                return new ArrayList<Object>(contentConnector.getContainer().getItemIds());
            }

        });
        c.setThumbnailHeight(100);
        c.setThumbnailWidth(100);
        return c;
    }

    @Override
    public void refresh() {
        ((InstagramContainer) contentConnector.getContainer()).refresh();
        super.refresh();
    }
}
