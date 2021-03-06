package org.deri.grefine.rdf.commands;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deri.grefine.rdf.app.ApplicationContext;
import org.deri.grefine.rdf.vocab.*;
import org.json.JSONWriter;


public class GetDefaultPrefixesCommand extends RdfCommand{

	public GetDefaultPrefixesCommand(ApplicationContext ctxt) {
		super(ctxt);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        try{
			getDefaultPrefixes(request, response);
        } catch (Exception e) {
            respondException(response, e);
        }
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json");
		try {
			getDefaultPrefixes(request, response);
		} catch (Exception e) {
			respondException(response, e);
		}
	}

	private void getDefaultPrefixes(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String projectId = request.getParameter("project");

		JSONWriter writer = new JSONWriter(response.getWriter());
		writer.object();
		writer.key("prefixes");
		writer.array();
		for (Vocabulary v : getRdfSchema(request).getPrefixesMap().values()) {
			try {
				getRdfContext().getVocabularySearcher().importAndIndexVocabulary(v.getName(), v.getUri(), v.getUri(), projectId, new VocabularyImporter());
			} catch (VocabularyImportException | VocabularyIndexException | PrefixExistException e) {
				logger.error("Error adding default prefix to project: " + e);
			}
			writer.object();
			writer.key("name");
			writer.value(v.getName());
			writer.key("uri");
			writer.value(v.getUri());
			writer.endObject();
		}
		writer.endArray();
		writer.endObject();
	}
	

}
