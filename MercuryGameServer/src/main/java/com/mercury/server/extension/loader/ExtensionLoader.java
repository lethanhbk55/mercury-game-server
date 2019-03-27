package com.mercury.server.extension.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.nhb.common.utils.FileSystemUtils;

final class ExtensionLoader {

	private ExtensionConfigReader configReader;
	private File extFolder;
	private URLClassLoader classLoader;
	private String name;

	ExtensionLoader(File extFolder) {
		this.extFolder = extFolder;
	}

	ExtensionLoader(String path) {
		this(new File(path));
	}

	public void load() throws Exception {
		if (extFolder.exists() && extFolder.isDirectory()) {
			// read config
			this.configReader = new ExtensionConfigReader();
			String filePath = FileSystemUtils.createPathFrom(extFolder.getAbsolutePath(), "mercury.plugin.xml");
			try {
				this.configReader.read(filePath);
			} catch (Exception e) {
				throw new RuntimeException("read file get error, file path " + filePath, e);
			}

			this.name = configReader.getPluginName();

			// load jar files
			File libFolder = new File(extFolder.getAbsolutePath(), "lib");
			if (libFolder.exists() && libFolder.isDirectory()) {
				List<File> jars = FileSystemUtils.scanFolder(libFolder);
				if (jars != null && jars.size() > 0) {
					URL[] urls = new URL[jars.size()];
					for (int i = 0; i < jars.size(); i++) {
						File jar = jars.get(i);
						try {
							urls[i] = jar.toURI().toURL();
						} catch (MalformedURLException e) {
							throw new RuntimeException("error while getting url for jar file " + jar.getAbsolutePath(),
									e);
						}
					}
					this.classLoader = new URLClassLoader(urls, getClass().getClassLoader());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <ClassType> ClassType loadClass(String className) throws ClassNotFoundException {
		if (className != null && className.trim().length() > 0) {
			Class<?> clazz = this.classLoader == null ? null : this.classLoader.loadClass(className);
			if (clazz == null) {
				clazz = this.getClass().getClassLoader().loadClass(className);
			}
			return (ClassType) clazz;
		}
		return null;
	}

	public <T> T newInstance(String className) throws Exception {
		Class<T> clazz = this.loadClass(className);
		if (clazz != null) {
			return clazz.newInstance();
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public ExtensionConfigReader getConfigReader() {
		return this.configReader;
	}
}
