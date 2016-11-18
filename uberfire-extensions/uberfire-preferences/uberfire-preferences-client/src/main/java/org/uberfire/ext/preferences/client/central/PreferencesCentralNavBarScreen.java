/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.preferences.client.central;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.uberfire.annotations.Customizable;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyStructurePresenter;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyStructureView;
import org.uberfire.ext.preferences.client.central.tree.TreeView;
import org.uberfire.ext.preferences.shared.CustomPreferenceScopeResolutionStrategyInfoFactory;
import org.uberfire.ext.preferences.shared.bean.PreferenceBeanServerStore;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.CompassPosition;

@WorkbenchScreen(identifier = PreferencesCentralNavBarScreen.IDENTIFIER)
public class PreferencesCentralNavBarScreen {

    public static final String IDENTIFIER = "PreferencesCentralNavBarScreen";

    private final HierarchyStructurePresenter hierarchyStructurePresenter;

    private final Caller<PreferenceBeanServerStore> preferenceBeanServerStoreCaller;

    private final Event<NotificationEvent> notification;

    private final CustomPreferenceScopeResolutionStrategyInfoFactory scopeResolutionStrategyInfoFactory;

    @Inject
    public PreferencesCentralNavBarScreen( @TreeView final HierarchyStructurePresenter treePresenter,
                                           final Caller<PreferenceBeanServerStore> preferenceBeanServerStoreCaller,
                                           final Event<NotificationEvent> notification,
                                           @Customizable final CustomPreferenceScopeResolutionStrategyInfoFactory scopeResolutionStrategyInfoFactory ) {
        this.hierarchyStructurePresenter = treePresenter;
        this.preferenceBeanServerStoreCaller = preferenceBeanServerStoreCaller;
        this.notification = notification;
        this.scopeResolutionStrategyInfoFactory = scopeResolutionStrategyInfoFactory;
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {

        final String preferenceIdentifier = placeRequest.getParameter( "identifier", null );
        final String customScopeResolutionStrategy = placeRequest.getParameter( "customScopeResolutionStrategy", "false" );

        if ( Boolean.valueOf( customScopeResolutionStrategy ) != null ) {
            final Map<String, String> params = new HashMap<>( placeRequest.getParameters() );
            cleanParametersToCreateScopeResolutionStrategy( params );
            final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo = scopeResolutionStrategyInfoFactory.create( params );
            hierarchyStructurePresenter.init( preferenceIdentifier, scopeResolutionStrategyInfo );
        } else {
            hierarchyStructurePresenter.init( preferenceIdentifier );
        }
    }

    private void cleanParametersToCreateScopeResolutionStrategy( final Map<String, String> params ) {
        params.remove( "identifier" );
        params.remove( "title" );
        params.remove( "screen" );
        params.remove( "customScopeResolutionStrategy" );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Preferences Central";
    }

    @WorkbenchPartView
    public HierarchyStructureView getView() {
        return hierarchyStructurePresenter.getView();
    }

    @DefaultPosition
    public CompassPosition getDefaultPosition() {
        return CompassPosition.WEST;
    }
}