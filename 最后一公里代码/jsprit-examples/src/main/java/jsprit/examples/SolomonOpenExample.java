/*******************************************************************************
 * Copyright (C) 2014  Stefan Schroeder
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package jsprit.examples;

import jsprit.analysis.toolbox.GraphStreamViewer;
import jsprit.analysis.toolbox.GraphStreamViewer.Label;
import jsprit.analysis.toolbox.Plotter;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.Jsprit;
import jsprit.core.algorithm.selector.SelectBest;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.io.VrpXMLReader;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.util.Examples;

import java.util.Collection;


public class SolomonOpenExample {

    public static void main(String[] args) {
        /*
         * some preparation - create output folder
		 */
        Examples.createOutputFolder();

		/*
         * Build the problem.
		 *
		 * But define a problem-builder first.
		 */
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

		/*
         * A solomonReader reads solomon-instance files, and stores the required information in the builder.
		 */
        new VrpXMLReader(vrpBuilder).read("input/deliveries_solomon_open_c101.xml");

		/*
         * Finally, the problem can be built. By default, transportCosts are crowFlyDistances (as usually used for vrp-instances).
		 */
        VehicleRoutingProblem vrp = vrpBuilder.build();

        new Plotter(vrp).plot("output/solomon_C101_open.png", "C101");

		/*
         * Define the required vehicle-routing algorithms to solve the above problem.
		 *
		 * The algorithm can be defined and configured in an xml-file.
		 */
//		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
//		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "input/algorithmConfig_fix.xml");
        VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
//		vra.setPrematureBreak(100);
//		vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("output/sol_progress.png"));
        /*
         * Solve the problem.
		 *
		 *
		 */
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

		/*
         * Retrieve best solution.
		 */
        VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);

		/*
		 * print solution
		 */
        SolutionPrinter.print(solution);

		/*
		 * Plot solution.
		 */
//		SolutionPlotter.plotSolutionAsPNG(vrp, solution, "output/solomon_C101_open_solution.png","C101");


        new GraphStreamViewer(vrp, solution).setRenderDelay(150).labelWith(Label.ID).setEnableAutoLayout(true).display();


    }

}
