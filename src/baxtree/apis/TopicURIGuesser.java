package baxtree.apis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import RDFaAnnotator.main.RDFModelLoader;
import baxtree.btr.ImportanceCalculator;
import baxtree.btr.LabelFinder;
import baxtree.btr.NodeStatist;

import com.hp.hpl.jena.rdf.model.Model;

public class TopicURIGuesser extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		String rdf_url = request.getParameter("rdfurl");
		String index = request.getParameter("index");
//		System.out.println(rdf_url);
		Model model;
		if(RDFModelLoader.isValidURL(rdf_url))
			model = RDFModelLoader.loadTriplesFromURL(rdf_url);
		else
			model = RDFModelLoader.loadTriplesFromString(rdf_url);
		NodeStatist ns = new NodeStatist(model);
		ArrayList<String> potential_topics = ImportanceCalculator.getTopNUriWithLargestImportance(3, ns.getUri_occurrence_in_sub(), ns.getUri_occurrence_in_obj(), 0.5);
		String result = "";
		for(String purl : potential_topics){
			String preferred_label = LabelFinder.getPreferredLabel(model, purl);
			if(preferred_label != null)
				result += "<a href=\"javascript:select_topic('" + purl + "', '" + index + "');\">" + preferred_label + "</a>,";
			else
				result += "<a href=\"javascript:select_topic('" + purl + "', '" + index + "');\">" + purl + "</a>,";
		} 
		System.out.println(index);
		result = result.substring(0, result.length()-1);
		System.out.println(result);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(result);
	}
}
