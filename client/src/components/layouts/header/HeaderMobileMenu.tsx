"use client";

import { Button } from "@/components/ui/button";
import { Sheet, SheetClose, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { DialogDescription, DialogTitle } from "@radix-ui/react-dialog";
import { HamburgerMenuIcon } from "@radix-ui/react-icons";
import Link from "next/link";

interface NavLinkProps {
  label: string;
  link: string;
  pathname: string;
}

const NavLink = ({ label, link, pathname }: NavLinkProps) => (
  <SheetClose asChild>
    <Link
      href={link}
      className={`${pathname === link ? "text-foregound" : "text-foreground/60"} p-1 text-lg font-light hover:text-foreground/80 transition-colors`}
    >
      {label}
    </Link>
  </SheetClose>
);

export const HeaderMobileMenu = ({ pathname }: { pathname: string }) => (
  <Sheet>
    <SheetTrigger className="block sm:hidden" asChild>
      <Button variant="transparent" size="icon" className="cursor-pointer">
        <HamburgerMenuIcon className="h-[1.2rem] w-[1.2rem]" />
      </Button>
    </SheetTrigger>
    <SheetContent side="left">
      <SheetClose asChild>
        <Link className="text-xl font-semibold text-left" href="/">
          <DialogTitle>42Stats</DialogTitle>
          <DialogDescription className="text-sm font-light text-muted-foreground">Statistics for 42 students</DialogDescription>
        </Link>
      </SheetClose>
      <div className="mt-1 flex-col flex justify-start items-start gap-0">
        <NavLink label="Leaderboard" link="/leaderboard" pathname={pathname} />
        <NavLink label="Statistics" link="/stats" pathname={pathname} />
      </div>
    </SheetContent>
  </Sheet>
);
