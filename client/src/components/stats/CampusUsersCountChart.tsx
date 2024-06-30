"use client";

import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { getCampuses } from "@/services/campus.service";
import { Campus } from "@/types/campus.interface";
import { useEffect, useState } from "react";
import { HorizontalBarChart } from "./HorizontalBarChart";

export const CampusUsersCountChart = () => {
  const [campuses, setCampuses] = useState<Campus[]>([]);
  const [campusesNames, setCampusesNames] = useState<string[]>([]);
  const [campusesUsersCount, setCampusesUsersCount] = useState<number[]>([]);
  const [campusesStudentsCount, setCampusesStudentsCount] = useState<number[]>([]);

  useEffect(() => {
    const fetchCampuses = async () => {
      const campuses = await getCampuses();
      if (!campuses || campuses.length === 0) {
        return;
      }

      campuses.sort((a: Campus, b: Campus) => b.studentCount - a.studentCount);
      setCampuses(campuses);
      setCampusesNames(campuses.map((campus: Campus) => campus.name + ` (${campus.country})`));
      setCampusesUsersCount(campuses.map((campus: Campus) => campus.userCount));
      setCampusesStudentsCount(campuses.map((campus: Campus) => campus.studentCount));
    };

    fetchCampuses();
  }, []);

  return (
    <Tabs defaultValue="studentCount" className="w-full flex flex-col justify-center items-center">
      <TabsList className="h-full flex flex-col md:flex-row">
        <TabsTrigger value="studentCount" className="w-full">
          Student count
        </TabsTrigger>
        <TabsTrigger value="averageLevel" className="w-full">
          Average level
        </TabsTrigger>
      </TabsList>
      <TabsContent value="studentCount" className="w-full">
        <HorizontalBarChart label="Students" campusesNames={campusesNames} campusesLevels={campusesStudentsCount} />
      </TabsContent>
      <TabsContent value="averageLevel" className="w-full">
        {/* <HorizontalBarChart label="Average level" campusesNames={campusesNames} campusesLevels={campusesAvgLevels} /> */}
      </TabsContent>
    </Tabs>
  );
};
