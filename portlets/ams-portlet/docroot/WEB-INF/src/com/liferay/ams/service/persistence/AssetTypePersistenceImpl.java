/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.ams.service.persistence;

import com.liferay.ams.NoSuchAssetTypeException;
import com.liferay.ams.model.AssetType;
import com.liferay.ams.model.impl.AssetTypeImpl;
import com.liferay.ams.model.impl.AssetTypeModelImpl;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.annotation.BeanReference;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.persistence.BatchSessionUtil;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <a href="AssetTypePersistenceImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class AssetTypePersistenceImpl extends BasePersistenceImpl
	implements AssetTypePersistence {
	public AssetType create(long assetTypeId) {
		AssetType assetType = new AssetTypeImpl();

		assetType.setNew(true);
		assetType.setPrimaryKey(assetTypeId);

		return assetType;
	}

	public AssetType remove(long assetTypeId)
		throws NoSuchAssetTypeException, SystemException {
		Session session = null;

		try {
			session = openSession();

			AssetType assetType = (AssetType)session.get(AssetTypeImpl.class,
					new Long(assetTypeId));

			if (assetType == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("No AssetType exists with the primary key " +
						assetTypeId);
				}

				throw new NoSuchAssetTypeException(
					"No AssetType exists with the primary key " + assetTypeId);
			}

			return remove(assetType);
		}
		catch (NoSuchAssetTypeException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public AssetType remove(AssetType assetType) throws SystemException {
		for (ModelListener listener : listeners) {
			listener.onBeforeRemove(assetType);
		}

		assetType = removeImpl(assetType);

		for (ModelListener listener : listeners) {
			listener.onAfterRemove(assetType);
		}

		return assetType;
	}

	protected AssetType removeImpl(AssetType assetType)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			if (BatchSessionUtil.isEnabled()) {
				Object staleObject = session.get(AssetTypeImpl.class,
						assetType.getPrimaryKeyObj());

				if (staleObject != null) {
					session.evict(staleObject);
				}
			}

			session.delete(assetType);

			session.flush();

			return assetType;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);

			FinderCacheUtil.clearCache(AssetType.class.getName());
		}
	}

	public AssetType update(AssetType assetType) throws SystemException {
		if (_log.isWarnEnabled()) {
			_log.warn(
				"Using the deprecated update(AssetType assetType) method. Use update(AssetType assetType, boolean merge) instead.");
		}

		return update(assetType, false);
	}

	public AssetType update(AssetType assetType, boolean merge)
		throws SystemException {
		boolean isNew = assetType.isNew();

		for (ModelListener listener : listeners) {
			if (isNew) {
				listener.onBeforeCreate(assetType);
			}
			else {
				listener.onBeforeUpdate(assetType);
			}
		}

		assetType = updateImpl(assetType, merge);

		for (ModelListener listener : listeners) {
			if (isNew) {
				listener.onAfterCreate(assetType);
			}
			else {
				listener.onAfterUpdate(assetType);
			}
		}

		return assetType;
	}

	public AssetType updateImpl(com.liferay.ams.model.AssetType assetType,
		boolean merge) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			BatchSessionUtil.update(session, assetType, merge);

			assetType.setNew(false);

			return assetType;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);

			FinderCacheUtil.clearCache(AssetType.class.getName());
		}
	}

	public AssetType findByPrimaryKey(long assetTypeId)
		throws NoSuchAssetTypeException, SystemException {
		AssetType assetType = fetchByPrimaryKey(assetTypeId);

		if (assetType == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No AssetType exists with the primary key " +
					assetTypeId);
			}

			throw new NoSuchAssetTypeException(
				"No AssetType exists with the primary key " + assetTypeId);
		}

		return assetType;
	}

	public AssetType fetchByPrimaryKey(long assetTypeId)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			return (AssetType)session.get(AssetTypeImpl.class,
				new Long(assetTypeId));
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<Object> findWithDynamicQuery(DynamicQuery dynamicQuery)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			dynamicQuery.compile(session);

			return dynamicQuery.list();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<Object> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			dynamicQuery.setLimit(start, end);

			dynamicQuery.compile(session);

			return dynamicQuery.list();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<AssetType> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	public List<AssetType> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	public List<AssetType> findAll(int start, int end, OrderByComparator obc)
		throws SystemException {
		boolean finderClassNameCacheEnabled = AssetTypeModelImpl.CACHE_ENABLED;
		String finderClassName = AssetType.class.getName();
		String finderMethodName = "findAll";
		String[] finderParams = new String[] {
				"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			};
		Object[] finderArgs = new Object[] {
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCacheUtil.getResult(finderClassName,
					finderMethodName, finderParams, finderArgs, this);
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringBuilder query = new StringBuilder();

				query.append("FROM com.liferay.ams.model.AssetType ");

				if (obc != null) {
					query.append("ORDER BY ");
					query.append(obc.getOrderBy());
				}

				else {
					query.append("ORDER BY ");

					query.append("name ASC");
				}

				Query q = session.createQuery(query.toString());

				List<AssetType> list = null;

				if (obc == null) {
					list = (List<AssetType>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);
				}
				else {
					list = (List<AssetType>)QueryUtil.list(q, getDialect(),
							start, end);
				}

				FinderCacheUtil.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, list);

				return list;
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return (List<AssetType>)result;
		}
	}

	public void removeAll() throws SystemException {
		for (AssetType assetType : findAll()) {
			remove(assetType);
		}
	}

	public int countAll() throws SystemException {
		boolean finderClassNameCacheEnabled = AssetTypeModelImpl.CACHE_ENABLED;
		String finderClassName = AssetType.class.getName();
		String finderMethodName = "countAll";
		String[] finderParams = new String[] {  };
		Object[] finderArgs = new Object[] {  };

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCacheUtil.getResult(finderClassName,
					finderMethodName, finderParams, finderArgs, this);
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(
						"SELECT COUNT(*) FROM com.liferay.ams.model.AssetType");

				Long count = null;

				Iterator<Long> itr = q.list().iterator();

				if (itr.hasNext()) {
					count = itr.next();
				}

				if (count == null) {
					count = new Long(0);
				}

				FinderCacheUtil.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, count);

				return count.intValue();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return ((Long)result).intValue();
		}
	}

	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.ams.model.AssetType")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener> listenersList = new ArrayList<ModelListener>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener)Class.forName(
							listenerClassName).newInstance());
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	@BeanReference(name = "com.liferay.ams.service.persistence.AssetCheckoutPersistence.impl")
	protected com.liferay.ams.service.persistence.AssetCheckoutPersistence assetCheckoutPersistence;
	@BeanReference(name = "com.liferay.ams.service.persistence.AssetDefinitionPersistence.impl")
	protected com.liferay.ams.service.persistence.AssetDefinitionPersistence assetDefinitionPersistence;
	@BeanReference(name = "com.liferay.ams.service.persistence.AssetEntryPersistence.impl")
	protected com.liferay.ams.service.persistence.AssetEntryPersistence assetEntryPersistence;
	@BeanReference(name = "com.liferay.ams.service.persistence.AssetTypePersistence.impl")
	protected com.liferay.ams.service.persistence.AssetTypePersistence assetTypePersistence;
	private static Log _log = LogFactoryUtil.getLog(AssetTypePersistenceImpl.class);
}